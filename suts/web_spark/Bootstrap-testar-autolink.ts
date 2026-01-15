import crypto from "crypto"
import ioredis from "ioredis"
import { MongoClient, ObjectID } from "mongodb"
import nano from "nano"
import { customAlphabet } from "nanoid"
import { Client, connect, Payload } from "ts-nats"
import { Participant } from "../model"
import { initializeQueues, PushNotificationQueue } from "../utils/queue/Queue"
import {
  ActivityEventRepository,
  ActivityRepository,
  ActivitySpecRepository,
  CredentialRepository,
  OrganizationRepository,
  ParticipantRepository,
  ProjectRoleRepository,
  ProjectUserRepository,
  ResearcherRepository,
  SensorEventRepository,
  SensorRepository,
  SensorSpecRepository,
  StudyRepository,
  TriggerGroupRepository,
  TriggerRepository,
  TypeRepository,
} from "./couch"
import {
  ActivityEventInterface,
  ActivityInterface,
  ActivitySpecInterface,
  CredentialInterface,
  OrganizationInterface,
  ParticipantInterface,
  ProjectRoleInterface,
  ProjectUserInterface,
  ResearcherInterface,
  SensorEventInterface,
  SensorInterface,
  SensorSpecInterface,
  StudyInterface,
  TriggerGroupInterface,
  TriggerInterface,
  TypeInterface,
} from "./interface/RepositoryInterface"
import {
  ActivityEventRepository as ActivityEventRepositoryMongo,
  ActivityRepository as ActivityRepositoryMongo,
  ActivitySpecRepository as ActivitySpecRepositoryMongo,
  CredentialRepository as CredentialRepositoryMongo,
  OrganizationRepository as OrganizationRepositoryMongo,
  ParticipantRepository as ParticipantRepositoryMongo,
  ProjectRoleRepository as ProjectRoleRepositoryMongo,
  ProjectUserRepository as ProjectUserRepositoryMongo,
  ResearcherRepository as ResearcherRepositoryMongo,
  SensorEventRepository as SensorEventRepositoryMongo,
  SensorRepository as SensorRepositoryMongo,
  SensorSpecRepository as SensorSpecRepositoryMongo,
  StudyRepository as StudyRepositoryMongo,
  TriggerGroupRepository as TriggerGroupRepositoryMongo,
  TriggerRepository as TriggerRepositoryMongo,
  TypeRepository as TypeRepositoryMongo
} from "./mongo"
export let RedisClient: ioredis.Redis
export let nc: Client
export let MongoClientDB: any
export const ApiResponseHeaders = {
  "Cache-Control": "no-store",
  "Content-Security-Policy":"default-src 'self'",
  "X-XSS-Protection":" 1; mode=block",
  "X-Content-Type-Options":"nosniff",
  "X-Frame-Options":"deny",
  "Strict-Transport-Security":"max-age=31536000; includeSubdomains"
}
//initialize driver for db
let DB_DRIVER = ""
//Identifying the Database driver -- IF the DB in env starts with mongodb://, create mongodb connection
//--ELSEIF the DB/CDB in env starts with http or https, create couch db connection
if (process.env.DB?.startsWith("mongodb://")) {
  DB_DRIVER = "mongodb"
} else if (process.env.DB?.startsWith("http") || process.env.DB?.startsWith("https")) {
  DB_DRIVER = "couchdb"
  console.log(`COUCHDB adapter in use `)
} else {
  if (process.env.CDB?.startsWith("http") || process.env.CDB?.startsWith("https")) {
    DB_DRIVER = "couchdb"
    console.log(`COUCHDB adapter in use `)
  } else {
    console.log(`Missing repository adapter.`)
  }
}

//IF the DB/CDB in env starts with http or https, create and export couch db connection
export const Database: any =
  process.env.DB?.startsWith("http") || process.env.DB?.startsWith("https")
    ? nano(process.env.DB ?? "")
    : process.env.CDB?.startsWith("http") || process.env.CDB?.startsWith("https")
    ? nano(process.env.CDB ?? "")
    : ""

export const uuid = customAlphabet("1234567890abcdefghjkmnpqrstvwxyz", 20)
export const numeric_uuid = (): string => `U${Math.random().toFixed(10).slice(2, 12)}`
//Initialize redis client for cacheing purpose

/**
 * If the data could not be encrypted or is invalid, returns `undefined`.
 */
export const Encrypt = (data: string, mode: "Rijndael" | "AES256" = "Rijndael"): string | undefined => {
  try {
    if (mode === "Rijndael") {
      const cipher = crypto.createCipheriv("aes-256-ecb", process.env.DB_KEY || "", "")
      return cipher.update(data, "utf8", "base64") + cipher.final("base64")
    } else if (mode === "AES256") {
      const ivl = crypto.randomBytes(16)
      const cipher = crypto.createCipheriv(
        "aes-256-cbc", 
        process.env.ROOT_KEY || "",
        ivl
      ) 
      return Buffer.concat([ivl, cipher.update(Buffer.from(data, "utf16le")), cipher.final()]).toString("base64")
    }
  } catch (e) {console.error("Encrypt: " + e)}
  return undefined
}

/**
 * If the data could not be decrypted or is invalid, returns `undefined`.
 */
export const Decrypt = (data: string, mode: "Rijndael" | "AES256" = "Rijndael"): string | undefined => {
  try {
    if (mode === "Rijndael") {
      const cipher = crypto.createDecipheriv("aes-256-ecb", process.env.DB_KEY || "", "")
      return cipher.update(data, "base64", "utf8") + cipher.final("utf8")
    } else if (mode === "AES256") {
      const dat = Buffer.from(data, "base64")
      const cipher = crypto.createDecipheriv(
        "aes-256-cbc",
        Buffer.from(process.env.ROOT_KEY || ""),
        dat.slice(0, 16)
      )
      let out = Buffer.concat([cipher.update(dat.slice(16)), cipher.final()]).toString("utf16le")
      // console.log("decrypted:", out)
      return out
    }
  } catch (e) { console.log(e) }
  return undefined
}

// Initialize the CouchDB databases if any of them do not exist.
export async function Bootstrap(): Promise<void> {
  if (typeof process.env.REDIS_HOST === "string") {
    try {
      RedisClient = RedisFactory.getInstance()      
      console.log("Trying to connect redis")
      RedisClient.on("connect", () => {
        console.log("Connected to redis")
        initializeQueues()
      })
      RedisClient.on("error", async (err: any) => {
        console.log("redis connection error", err)
        RedisClient = RedisFactory.getInstance()
      })
      RedisClient.on("disconnected", async () => {
        console.log("redis disconnected")
        RedisClient = RedisFactory.getInstance()
      })  
    } catch (err) {
      console.log("Error initializing redis", err)
    }
  }  
  await NatsConnect()
  CheckBeepedActivity()
  CheckRepeatActivity()
  
  if (DB_DRIVER === "couchdb") {
    console.group("Initializing database connection...")
    const _db_list = await Database.db.list()
    if (!_db_list.includes("activity_spec")) {
      console.log("Initializing ActivitySpec database...")
      await Database.db.create("activity_spec")
    }
    console.log("ActivitySpec database online.")
    if (!_db_list.includes("sensor_spec")) {
      console.log("Initializing SensorSpec database...")
      await Database.db.create("sensor_spec")
    }
    console.log("SensorSpec database online.")
    if (!_db_list.includes("researcher")) {
      console.log("Initializing Researcher database...")
      await Database.db.create("researcher")
      Database.use("researcher").bulk({
        docs: [
          {
            _id: "_design/timestamp-index",
            language: "query",
            views: {
              timestamp: {
                map: {
                  fields: {
                    timestamp: "asc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: ["timestamp"],
                  },
                },
              },
            },
          },
          {
            _id: "_design/parent-timestamp-index",
            language: "query",
            views: {
              "parent-timestamp": {
                map: {
                  fields: {
                    "#parent": "asc",
                    timestamp: "asc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: ["#parent", "timestamp"],
                  },
                },
              },
            },
          },
          {
            _id: "_design/id-parent-timestamp-index",
            language: "query",
            views: {
              "id-parent-timestamp": {
                map: {
                  fields: {
                    _id: "asc",
                    "#parent": "asc",
                    timestamp: "asc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: ["_id", "#parent", "timestamp"],
                  },
                },
              },
            },
          },
        ],
      })
    }
    console.log("Researcher database online.")
    if (!_db_list.includes("study")) {
      console.log("Initializing Study database...")
      await Database.db.create("study")
      Database.use("study").bulk({
        docs: [
          {
            _id: "_design/timestamp-index",
            language: "query",
            views: {
              timestamp: {
                map: {
                  fields: {
                    timestamp: "asc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: ["timestamp"],
                  },
                },
              },
            },
          },
          {
            _id: "_design/parent-timestamp-index",
            language: "query",
            views: {
              "parent-timestamp": {
                map: {
                  fields: {
                    "#parent": "asc",
                    timestamp: "asc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: ["#parent", "timestamp"],
                  },
                },
              },
            },
          },
          {
            _id: "_design/id-parent-timestamp-index",
            language: "query",
            views: {
              "id-parent-timestamp": {
                map: {
                  fields: {
                    _id: "asc",
                    "#parent": "asc",
                    timestamp: "asc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: ["_id", "#parent", "timestamp"],
                  },
                },
              },
            },
          },
        ],
      })
    }
    console.log("Study database online.")
    if (!_db_list.includes("participant")) {
      console.log("Initializing Participant database...")
      await Database.db.create("participant")
      Database.use("participant").bulk({
        docs: [
          {
            _id: "_design/timestamp-index",
            language: "query",
            views: {
              timestamp: {
                map: {
                  fields: {
                    timestamp: "asc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: ["timestamp"],
                  },
                },
              },
            },
          },
          {
            _id: "_design/parent-timestamp-index",
            language: "query",
            views: {
              "parent-timestamp": {
                map: {
                  fields: {
                    "#parent": "asc",
                    timestamp: "asc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: ["#parent", "timestamp"],
                  },
                },
              },
            },
          },
          {
            _id: "_design/id-parent-timestamp-index",
            language: "query",
            views: {
              "id-parent-timestamp": {
                map: {
                  fields: {
                    _id: "asc",
                    "#parent": "asc",
                    timestamp: "asc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: ["_id", "#parent", "timestamp"],
                  },
                },
              },
            },
          },
        ],
      })
    }
    console.log("Participant database online.")
    if (!_db_list.includes("activity")) {
      console.log("Initializing Activity database...")
      await Database.db.create("activity")
      Database.use("activity").bulk({
        docs: [
          {
            _id: "_design/timestamp-index",
            language: "query",
            views: {
              timestamp: {
                map: {
                  fields: {
                    timestamp: "asc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: ["timestamp"],
                  },
                },
              },
            },
          },
          {
            _id: "_design/parent-timestamp-index",
            language: "query",
            views: {
              "parent-timestamp": {
                map: {
                  fields: {
                    "#parent": "asc",
                    timestamp: "asc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: ["#parent", "timestamp"],
                  },
                },
              },
            },
          },
          {
            _id: "_design/id-parent-timestamp-index",
            language: "query",
            views: {
              "id-parent-timestamp": {
                map: {
                  fields: {
                    _id: "asc",
                    "#parent": "asc",
                    timestamp: "asc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: ["_id", "#parent", "timestamp"],
                  },
                },
              },
            },
          },
          {
            _id: "_design/id-timestamp-index",
            language: "query",
            views: {
              "id-timestamp": {
                map: {
                  fields: {
                    _id: "asc",
                    timestamp: "asc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: ["_id", "timestamp"],
                  },
                },
              },
            },
          },
        ],
      })
    }
    console.log("Activity database online.")
    if (!_db_list.includes("sensor")) {
      console.log("Initializing Sensor database...")
      await Database.db.create("sensor")
      Database.use("sensor").bulk({
        docs: [
          {
            _id: "_design/timestamp-index",
            language: "query",
            views: {
              timestamp: {
                map: {
                  fields: {
                    timestamp: "asc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: ["timestamp"],
                  },
                },
              },
            },
          },
          {
            _id: "_design/parent-timestamp-index",
            language: "query",
            views: {
              "parent-timestamp": {
                map: {
                  fields: {
                    "#parent": "asc",
                    timestamp: "asc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: ["#parent", "timestamp"],
                  },
                },
              },
            },
          },
          {
            _id: "_design/id-parent-timestamp-index",
            language: "query",
            views: {
              "id-parent-timestamp": {
                map: {
                  fields: {
                    _id: "asc",
                    "#parent": "asc",
                    timestamp: "asc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: ["_id", "#parent", "timestamp"],
                  },
                },
              },
            },
          },
        ],
      })
    }
    console.log("Sensor database online.")
    if (!_db_list.includes("activity_event")) {
      console.log("Initializing ActivityEvent database...")
      await Database.db.create("activity_event")
      Database.use("activity_event").bulk({
        docs: [
          {
            _id: "_design/parent-activity-timestamp-index",
            language: "query",
            views: {
              "parent-activity-timestamp": {
                map: {
                  fields: {
                    "#parent": "desc",
                    activity: "desc",
                    timestamp: "desc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: [
                      {
                        "#parent": "desc",
                      },
                      {
                        activity: "desc",
                      },
                      {
                        timestamp: "desc",
                      },
                    ],
                  },
                },
              },
            },
          },
          {
            _id: "_design/parent-timestamp-index",
            language: "query",
            views: {
              "parent-timestamp": {
                map: {
                  fields: {
                    "#parent": "desc",
                    timestamp: "desc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: [
                      {
                        "#parent": "desc",
                      },
                      {
                        timestamp: "desc",
                      },
                    ],
                  },
                },
              },
            },
          },
        ],
      })
    }
    console.log("ActivityEvent database online.")
    if (!_db_list.includes("sensor_event")) {
      console.log("Initializing SensorEvent database...")
      await Database.db.create("sensor_event")
      Database.use("sensor_event").bulk({
        docs: [
          {
            _id: "_design/parent-sensor-timestamp-index",
            language: "query",
            views: {
              "parent-sensor-timestamp": {
                map: {
                  fields: {
                    "#parent": "desc",
                    sensor: "desc",
                    timestamp: "desc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: [
                      {
                        "#parent": "desc",
                      },
                      {
                        sensor: "desc",
                      },
                      {
                        timestamp: "desc",
                      },
                    ],
                  },
                },
              },
            },
          },
          {
            _id: "_design/parent-timestamp-index",
            language: "query",
            views: {
              "parent-timestamp": {
                map: {
                  fields: {
                    "#parent": "desc",
                    timestamp: "desc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: [
                      {
                        "#parent": "desc",
                      },
                      {
                        timestamp: "desc",
                      },
                    ],
                  },
                },
              },
            },
          },
        ],
      })
    }
    console.log("SensorEvent database online.")
    if (!_db_list.includes("credential")) {
      console.log("Initializing Credential database...")
      await Database.db.create("credential")
      Database.use("credential").bulk({
        docs: [
          {
            _id: "_design/access_key-index",
            language: "query",
            views: {
              access_key: {
                map: {
                  fields: {
                    access_key: "asc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: ["access_key"],
                  },
                },
              },
            },
          },
          {
            _id: "_design/origin-index",
            language: "query",
            views: {
              origin: {
                map: {
                  fields: {
                    origin: "asc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: ["origin"],
                  },
                },
              },
            },
          },
          {
            _id: "_design/origin-access_key-index",
            language: "query",
            views: {
              "origin-access_key": {
                map: {
                  fields: {
                    origin: "asc",
                    access_key: "asc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: ["origin", "access_key"],
                  },
                },
              },
            },
          },
        ],
      })
      console.dir(`An initial administrator password was generated and saved for this installation.`)
      try {
        // Create a new password and emit it to the console while saving it (to share it with the sysadmin).
        const p = crypto.randomBytes(32).toString("hex")
        console.table({ "Administrator Password": p })
        await Database.use("credential").insert({
          origin: null,
          access_key: "admin",
          secret_key: Encrypt(p, "AES256"),
          description: "System Administrator Credential",
        } as any)
      } catch (e) {
        console.dir(e)
      }
    }
    console.log("Credential database online.")
    if (!_db_list.includes("tag")) {
      console.log("Initializing Tag database...")
      await Database.db.create("tag")
      Database.use("tag").bulk({
        docs: [
          {
            _id: "_design/parent-type-key-index",
            language: "query",
            views: {
              "parent-type-key": {
                map: {
                  fields: {
                    "#parent": "asc",
                    type: "asc",
                    key: "asc",
                  },
                  partial_filter_selector: {},
                },
                reduce: "_count",
                options: {
                  def: {
                    fields: ["#parent", "type", "key"],
                  },
                },
              },
            },
          },
        ],
      })
    }
    console.log("Tag database online.")
    console.groupEnd()
    console.log("Database verification complete.")
  } else {
    //Connect to mongoDB
    const client = new MongoClient(`${process.env.DB}`, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    })
    try {
      await client.connect()
    } catch (error) {
      console.dir(error)
    }
    // return new Promise((resolve, reject) => {
    try {
      console.group("Initializing database connection...")
      if (client.isConnected()) {
        const db = process.env.DB?.split("/").reverse()[0]?.split("?")[0]
        MongoClientDB = await client?.db(db)
      } else {
        console.log("Database connection failed.")
      }
    } catch (error) {
      console.log("Database connection failed:", error)
    }
    //  })
    if (!!MongoClientDB) {
      console.group(`MONGODB adapter in use`)
      const DBs = await MongoClientDB.listCollections().toArray()
      const dbs: string[] = []
      for (const db of DBs) {
        await dbs.push(db.name)
      }

      // Preparing Mongo Collections
      if (!dbs.includes("activity_spec")) {
        console.log("Initializing ActivitySpec database...")
        await MongoClientDB.createCollection("activity_spec")
        const database = await MongoClientDB.collection("activity_spec")
        await database.createIndex({ timestamp: 1 })
      }
      console.log("ActivitySpec database online.")
      if (!dbs.includes("sensor_spec")) {
        console.log("Initializing SensorSpec database...")
        await MongoClientDB.createCollection("sensor_spec")
        const database = await MongoClientDB.collection("sensor_spec")
        await database.createIndex({ timestamp: 1 })

        // Programatically initialize SensorSpecs
        await database.insertOne({
          _id: "spark.gps",
          settings_schema: { frequency: 1 },
          _deleted: false,
        } as any)
        await database.insertOne({
          _id: "spark.steps",
          settings_schema: { frequency: 1 },
          _deleted: false,
        } as any)
      }
      console.log("SensorSpec database online.")

      const emptyOrganizationId = uuid()
      const testOrganizationId = uuid()

      if (!dbs.includes("organization")) {
        console.log("Initializing Organization database...")
        await MongoClientDB.createCollection("organization")
        const database = MongoClientDB.collection("organization")
        await database.createIndex({ _id: 1 }) // parent = organization_id

        // Creating a TEST ORGANIZATION for TESTAR-AUTOLINK
        console.dir(`An initial Organization was generated.`)
        try {

          await database.insertOne({
            _id: emptyOrganizationId,
            name: "Empty organization",
            language: "en",
            parent_organization : null,
            _deleted: false,
          } as any)

          await database.insertOne({
            _id: testOrganizationId,
            name: "Test organization",
            language: "en",
            parent_organization : null,
            _deleted: false,
          } as any)

        } catch (error) {
          console.log(error)
        }
      }
      console.log("Organization database online.")
      const researcherId1 = uuid()
      const researcherId2 = uuid()
      const sampleProjectId = uuid()
      if (!dbs.includes("researcher")) {
        console.log("Initializing Researcher database...")
        await MongoClientDB.createCollection("researcher")
        const database = MongoClientDB.collection("researcher")
        await database.createIndex({ _id: 1, _parent: 1, timestamp: 1 })
        await database.createIndex({ _parent: 1, timestamp: 1 })
        await database.createIndex({ timestamp: 1 })

        // Creating an user with test credentials with empty projects for TESTAR-AUTOLINK project
        try {

          await database.insertOne({
            _id: researcherId1,
            name: "EMPTY-User",
            organization_id: emptyOrganizationId,
            role: "admin",
            _parent: emptyOrganizationId,
            _deleted: false,
          } as any)

          await database.insertOne({
            _id: researcherId2,
            name: "TESTAR-User",
            organization_id: testOrganizationId,
            role: "admin",
            _parent: testOrganizationId,
            _deleted: false,
          } as any)

        } catch (error) {
          console.log(error)
        }
      }
      console.log("Researcher database online.")
      if (!dbs.includes("project_user")) {
        console.log("Initializing Project User database...")
        await MongoClientDB.createCollection("project_user")
        const database = MongoClientDB.collection("project_user")
        await database.createIndex({ _id: 1, _parent: 1 }) // parent = researcher_id
      }
      console.log("Project User database online.")
      if (!dbs.includes("project_role")) {
        console.log("Initializing Project Role database...")
        await MongoClientDB.createCollection("project_role")
        const database = MongoClientDB.collection("project_role")
        await database.createIndex({ _id: 1 })
      }
      console.log("Project Role database online.")
      // TODO: is this redundant?
      // if (!dbs.includes("project_permission")) {
      //   console.log("Initializing Project Permission database...")
      //   await MongoClientDB.createCollection("project_permission")
      //   const database = MongoClientDB.collection("project_permission")
      //   await database.createIndex({ _id: 1 })
      // }
      // console.log("Project Permission database online.")
      
      if (!dbs.includes("study")) {
        console.log("Initializing Study database...")
        await MongoClientDB.createCollection("study")
        const database = MongoClientDB.collection("study")
        await database.createIndex({ _id: 1, _parent: 1, timestamp: 1 })
        await database.createIndex({ _parent: 1, timestamp: 1 })
        await database.createIndex({ timestamp: 1 })

        // Creating a Sample PROJECT for TESTAR-AUTOLINK
        console.dir(`A Sample Project was generated.`)
        try {

          // We do not create any project for the empty organization

          // But we do for the test organization
          await database.insertOne({
            _id: sampleProjectId,
            _parent: testOrganizationId,
            name: "Sample Project",
            joinable: true,
            _deleted: false,
          } as any)

        } catch (error) {
          console.log(error)
        }
      }
      console.log("Study database online.")
      if (!dbs.includes("participant")) {
        console.log("Initializing Participant database...")
        await MongoClientDB.createCollection("participant")
        const database = MongoClientDB.collection("participant")
        await database.createIndex({ _id: 1, _parent: 1, timestamp: 1 })
        await database.createIndex({ _parent: 1, timestamp: 1 })
        await database.createIndex({ timestamp: 1 })
      }
      console.log("Participant database online.")
      if (!dbs.includes("activity")) {
        console.log("Initializing Activity database...")
        await MongoClientDB.createCollection("activity")
        const database = await MongoClientDB.collection("activity")
        await database.createIndex({ _id: 1, _parent: 1, timestamp: 1 })
        await database.createIndex({ _parent: 1, timestamp: 1 })
        await database.createIndex({ timestamp: 1 })
        await database.createIndex({ _id: 1, timestamp: 1 })
        
        // Creating a Default Survey ACTIVITY for TESTAR-AUTOLINK
        console.dir(`A Default Survey ACTIVITY was generated.`)
        try {

          // We do not create any survey for the empty organization (no project there)

          // But we do for the test organization (with sample project)
          await database.insertOne({
            _id: uuid(),
            _parent: sampleProjectId,
            timestamp: new Date().getTime(),
            spec: "spark.survey",
            name: "Default Survey",
            settings: [ { "text" : "This is a default survey.", "type" : "text", "required" : true } ],
            schedule: [],
            category: ["assess"],
            _deleted: false,
          } as any)

        } catch (error) {
          console.log(error)
        }
      }
      console.log("Activity database online.")
      if (!dbs.includes("sensor")) {
        console.log("Initializing Sensor database...")
        await MongoClientDB.createCollection("sensor")
        const database = MongoClientDB.collection("sensor")
        await database.createIndex({ _id: 1, _parent: 1, timestamp: 1 })
        await database.createIndex({ _parent: 1, timestamp: 1 })
        await database.createIndex({ timestamp: 1 })
      }
      console.log("Sensor database online.")
      if (!dbs.includes("activity_event")) {
        console.log("Initializing ActivityEvent database...")
        await MongoClientDB.createCollection("activity_event")
        const database = MongoClientDB.collection("activity_event")
        await database.createIndex({ _parent: -1, activity: -1, timestamp: -1 })
        await database.createIndex({ _parent: -1, timestamp: -1 })
      }
      console.log("ActivityEvent database online.")
      if (!dbs.includes("sensor_event")) {
        console.log("Initializing SensorEvent database...")
        await MongoClientDB.createCollection("sensor_event")
        const database = MongoClientDB.collection("sensor_event")
        await database.createIndex({ _parent: -1, sensor: -1, timestamp: -1 })
        await database.createIndex({ _parent: -1, timestamp: -1 })
      }
      console.log("SensorEvent database online.")
      if (!dbs.includes("trigger")) {
        console.log("Initializing Trigger database...")
        await MongoClientDB.createCollection("trigger")
        const database = MongoClientDB.collection("trigger")
        await database.createIndex({ _id: 1, _parent: 1 })
      }
      console.log("Trigger database online.")
      if (!dbs.includes("tag")) {
        console.log("Initializing Tag database...")
        await MongoClientDB.createCollection("tag")
        const database = MongoClientDB.collection("tag")
        await database.createIndex({ _parent: 1, type: 1, key: 1 })
      }
      console.log("Tag database online.")
      if (!dbs.includes("credential")) {
        console.log("Initializing Credential database...")
        await MongoClientDB.createCollection("credential")
        const database = MongoClientDB.collection("credential")
        await database.createIndex({ access_key: 1 })
        await database.createIndex({ origin: 1 })
        await database.createIndex({ origin: 1, access_key: 1 })

        console.dir(`An initial administrator password was generated and saved for this installation.`)
        try {
          // Create a new password and emit it to the console while saving it (to share it with the sysadmin).
          const p = crypto.randomBytes(32).toString("hex")
          console.table({ "Administrator Password": p })
          await database.insertOne({
            _id: new ObjectID(),
            origin: null,
            access_key: "admin",
            secret_key: Encrypt(p, "AES256"),
            description: "System Administrator Credential",
            _deleted: false,
          } as any)

          // Creating an empty user for TESTAR-AUTOLINK project
          await database.insertOne({
            _id: new ObjectID(),
            origin: researcherId1,
            access_key: "empty-user",
            secret_key: Encrypt("EmptyPass1!", "AES256"),
            description: "EMPTY-User",
            _deleted: false,
          } as any)

          // Creating a test user for TESTAR-AUTOLINK project
          await database.insertOne({
            _id: new ObjectID(),
            origin: researcherId2,
            access_key: "testar-user",
            secret_key: Encrypt("TestarPass1!", "AES256"),
            description: "TESTAR-User",
            _deleted: false,
          } as any)

        } catch (error) {
          console.log(error)
        }
      }
      console.log("Credential database online.")
      console.groupEnd()
      console.groupEnd()
      console.log("Database verification complete.")
    } else {
      console.groupEnd()
      console.log("Database verification failed.")
    }
  }
}


/**
 * nats connect
 */
 async function NatsConnect() {
  let intervalId = setInterval(async () => {
    try {
      nc = await connect({
        servers: [`${process.env.NATS_SERVER}`],
        payload: Payload.JSON,
        maxReconnectAttempts: -1,
        reconnect: true,
        reconnectTimeWait: 2000,
      })
      clearInterval(intervalId)
      console.log("Connected to nats pub server")    
    } catch (error) {
      console.log("Error in Connecting to nats pub server: ", error)
    }
  }, 3000)
}

function getTimezoneOffset(timeZone: string){
  const str = new Date().toLocaleString('en', {timeZone, timeZoneName: "short"});
  const h = str.match(/GMT([+-]\d+)/)?.[1];
  return parseInt(h? h : "") * -1;
}

async function CheckBeepedActivity() {
  let intervalId = setInterval(async () => {
    try {
      //get all activities
      const activities = await new Repository().getActivityRepository()._all()

      const currentDate = new Date()

      //loop through activities
      activities.forEach(async (activity: any) => {
        //check if activity has a beeped schedule
        if(activity.schedule.length > 0 && activity.schedule[0].beeped){
          const lastBeepedAtTime = new Date(activity.schedule[0]['last_beeped'] ?? 0).getTime()
          const dateEndBeep = new Date(lastBeepedAtTime + activity.schedule[0]['time_for_question'] * 60_000)

          //hardcoded timezone for now
          var timeOffset : number = getTimezoneOffset("Europe/Paris")

          // check if beep time has passed
          if(activity.schedule[0].current_beep 
            && currentDate > dateEndBeep){
            console.log("Unbeeping", activity.name)

            activity.schedule[0].current_beep = false
          }

          //check if beeped schedule is due according to time and day of the week
          else if(
            activity.schedule[0].current_beep != true 
              && (
                currentDate.getUTCHours() > (parseInt(activity.schedule[0].time.split(":")[0]) + timeOffset) 
                || (
                  currentDate.getUTCHours() == (parseInt(activity.schedule[0].time.split(":")[0]) + timeOffset)
                  && currentDate.getUTCMinutes() >= activity.schedule[0].time.split(":")[1]
                )
              ) 
              && (
                currentDate.getUTCHours() < (parseInt(activity.schedule[0].end_time.split(":")[0]) + timeOffset) 
                || (
                  currentDate.getUTCHours() == (parseInt(activity.schedule[0].end_time.split(":")[0]) + timeOffset)
                  && currentDate.getUTCMinutes() <= activity.schedule[0].end_time.split(":")[1]
                )
              )
          ) {
            if(currentDate.getDay() == 1 && activity.schedule[0].monday 
            || currentDate.getDay() == 2 && activity.schedule[0].tuesday
            || currentDate.getDay() == 3 && activity.schedule[0].wednesday
            || currentDate.getDay() == 4 && activity.schedule[0].thursday
            || currentDate.getDay() == 5 && activity.schedule[0].friday
            || currentDate.getDay() == 6 && activity.schedule[0].saturday
            || currentDate.getDay() == 0 && activity.schedule[0].sunday) {
              //check if there is a current beep interval set
              if (!activity.schedule[0].current_beep_interval || activity.schedule[0].current_beep_interval < 0) {
                //set beep interval random between min and max interval time
                activity.schedule[0].current_beep_interval = Math.round(Math.random() * (activity.schedule[0].max_beep_interval - activity.schedule[0].min_beep_interval)) + activity.schedule[0].min_beep_interval
              }

              if (!activity.schedule[0].current_beep_count) {
                activity.schedule[0].current_beep_count = 0
              }
      
              //check if current beep count is still below max beep count
              if(activity.schedule[0].current_beep_count < activity.schedule[0].beep_count) {
                // check when next beep should take place
                const nextBeepAt = new Date(lastBeepedAtTime + activity.schedule[0].current_beep_interval * 60000)

                // if next beeped is passed, beep
                if(currentDate > nextBeepAt){
                  activity.schedule[0].last_beeped = currentDate
                  activity.schedule[0].current_beep_count += 1
                  activity.schedule[0].current_beep_interval = Math.round(Math.random() * (activity.schedule[0].max_beep_interval - activity.schedule[0].min_beep_interval)) + activity.schedule[0].min_beep_interval

                  activity.schedule[0].current_beep = true

                  //grab participants of study
                  const participants = await new Repository().getParticipantRepository()._select(activity._parent, true)

                  //form participants payload
                  const newParticipants = await prepareParticipants(participants, activity.name, "new activity available", activity.id)
                  console.log("activity beeped!", activity.name)
                  //send notifications to participants of given study
                  sendToParticipants(newParticipants)
                }
              }
            }
          }
          // Save changes
          await new Repository().getActivityRepository()._update(activity.id, activity)
        }
      })
    
      console.log("checked beeped activities")
    } catch (error) {
      console.log("Error in checking beeped activities: ", error)
    }
  }, 60000)
}

async function CheckRepeatActivity() {
  let intervalId = setInterval(async () => {
    try {
      //get all activities
      const activities = await new Repository().getActivityRepository()._all()

      const currentDate = new Date()

      var timeOffset : number = getTimezoneOffset("Europe/Paris")

      //loop through activities
      activities.forEach(async (activity: any) => {
        //check if activity has a beeped schedule
        if(activity.schedule.length > 0 && activity.schedule[0].start_date && activity.schedule[0].time){
          const startDate = new Date(activity.schedule[0].start_date)
          startDate.setHours(startDate.getHours() + 5)
          //check if the survey is becoming first available
          if(currentDate.getFullYear() == startDate.getFullYear() && currentDate.getMonth() == startDate.getMonth() && currentDate.getDate() == startDate.getDate() && currentDate.getHours() == (parseInt(activity.schedule[0].time.split(":")[0]) + timeOffset) && currentDate.getMinutes() == parseInt(activity.schedule[0].time.split(":")[1])){
            //grab participants of study
            const participants = await new Repository().getParticipantRepository()._select(activity._parent, true)

            //form participants payload
            const newParticipants = await prepareParticipants(participants, activity.name, "new activity available", activity.id)
            console.log("scheduled activity first activation!")
            //send notifications to participants of given study
            sendToParticipants(newParticipants)
          }
          else if(activity.schedule[0].repeat_interval != "none" && (startDate.getFullYear() < currentDate.getFullYear() || (startDate.getFullYear() == currentDate.getFullYear() && (startDate.getMonth() < currentDate.getMonth() || (startDate.getMonth() == currentDate.getMonth() && (startDate.getDate() < currentDate.getDate() || (startDate.getDate() === currentDate.getDate() && ((parseInt(activity.schedule[0].time.split(':')[0]) + timeOffset) < currentDate.getHours() || ((parseInt(activity.schedule[0].time.split(':')[0]) + timeOffset) === currentDate.getHours() && parseInt(activity.schedule[0].time.split(':')[1]) < currentDate.getMinutes()))))))))){
            const endDate = new Date(activity.schedule[0].end_date)
            //check if no end date and otherwise currentDate isn't past the end date
            if(!activity.schedule[0].end_date || (currentDate.getFullYear() < endDate.getFullYear() || (currentDate.getFullYear() == endDate.getFullYear() && (currentDate.getMonth() < endDate.getMonth() || (currentDate.getMonth() == endDate.getMonth() && (currentDate.getDate() < endDate.getDate() || (currentDate.getDate() === endDate.getDate() && (parseInt(activity.schedule[0].end_time.split(':')[0]) < currentDate.getHours() || (parseInt(activity.schedule[0].end_time.split(':')[0]) === currentDate.getHours() && parseInt(activity.schedule[0].end_time.split(':')[1]) < currentDate.getMinutes()))))))))){
              //graph participants of study
              const participants = await new Repository().getParticipantRepository()._select(activity._parent, true)

              var filteredParticipants: Participant[] = []

              //filter participants based on when they last participated in the activity
              for(let x = 0; x < participants.length; x++){
                var from = new Date()
                switch(activity.schedule[0].repeat_interval){
                  case "daily":
                    from.setDate(from.getDate() - 1)
                    break;
                  case "weekly":
                    from.setDate(from.getDate() - 7)
                    break;
                  case "monthly":
                    from.setMonth(from.getMonth() - 1)
                    break;
                  default:
                    // unknown or none repeat value
                    break;
                }
                var till = new Date(from)
                till.setMinutes(till.getMinutes() + 1)
    
                //grab last responce from participant
                const answers = await new Repository().getActivityEventRepository()._select(participants[x].id, activity.id, from.getTime(), till.getTime(), 1)
                
                if(answers.length > 0){
                  filteredParticipants.push(participants[x])
                }
              }
              
              if(filteredParticipants.length > 0){
                //form participants payload
                const newParticipants = await prepareParticipants(filteredParticipants, activity.name, "new scheduled activity available", activity.id)
                console.log("scheduled activity activation!")
                //send notifications to participants of given study
                sendToParticipants(newParticipants)
              }
            }
          }
        }
        else if(activity.schedule.length > 0 && !activity.schedule[0].start_date && activity.schedule[0].repeat_interval != "none"){
          //graph participants of study
          const participants = await new Repository().getParticipantRepository()._select(activity._parent, true)

          var filteredParticipants: Participant[] = []

          //filter participants based on when they last participated in the activity
          for(let x = 0; x < participants.length; x++){
            var from = new Date()
            switch(activity.schedule[0].repeat_interval){
              case "daily":
                from.setDate(from.getDate() - 1)
                break;
              case "weekly":
                from.setDate(from.getDate() - 7)
                break;
              case "monthly":
                from.setMonth(from.getMonth() - 1)
                break;
              default:
                // unknown or none repeat value
                break;
            }
            var till = new Date(from)
            till.setMinutes(till.getMinutes() + 1)

            //grab last responce from participant
            const answers = await new Repository().getActivityEventRepository()._select(participants[x].id, activity.id, from.getTime(), till.getTime(), 1)

            if(answers.length > 0){
              filteredParticipants.push(participants[x])
            }
          }
          
          if(filteredParticipants.length > 0){
            //form participants payload
            const newParticipants = await prepareParticipants(filteredParticipants, activity.name, "new repeat activity available", activity.id)
            console.log("repeat activity activation!")
            //send notifications to participants of given study
            sendToParticipants(newParticipants)
          }
        }
      })
    
      console.log("check repeating activities")
    } catch (error) {
      console.log("Error in checking repeating activities: ", error)
    }
  }, 60000)
}

export async function sendToParticipants(Participants: any): Promise<void> {
  Participants = Array.isArray(Participants) ? Participants : [Participants]
  for (const participant of Participants) {
    try {
      const repo = new Repository()
      const SensorEventRepository = repo.getSensorEventRepository()
      const event_data = await SensorEventRepository._select(
        participant.participant_id,
        "spark.analytics",
        undefined,
        undefined,
        1000
      )
      if (event_data.length !== 0) {
        const filteredArray: any = await event_data.filter(
          (x: any) =>
            x.data.type === "login" &&
            x.data.device_token !== undefined
        )
        if (filteredArray.length !== 0) {
          const events: any = filteredArray[0]
          const device = undefined !== events && undefined !== events.data ? events.data : undefined
          if (device !== undefined && (device.device_type || device.device_token || participant.title)) {
            console.log("Sending push notification to: ", device.device_token)
            //add to PushNotificationQueue without schedule(immediate push would happen)
            PushNotificationQueue?.add(
              {
                device_type: device.device_type.toLowerCase(),
                device_token: device.device_token,
                payload: {
                  participant_id: participant.participant_id,
                  title: participant.title,
                  message: participant.message,
                },
              },
              { attempts: 3, backoff: 10, removeOnComplete: true, removeOnFail: true }
            )
          }
        }
      }
    } catch (error:any) {
      console.log(error)
    }
  }
}

/**
 *
 * @param Participants
 * @param title
 * @param message
 */
export async function prepareParticipants(Participants: any, title: any, message: any, customId?: any): Promise<any[]> {
  const newParticipants = []
  if (undefined === customId) customId = ""
  for (const ParticipantsData of Participants) {
    newParticipants.push({ participant_id: ParticipantsData.id, title: title, message: message, activity_id: customId })
  }
  return newParticipants
}

/**
 * GET THE REPOSITORY TO USE(Mongo/Couch)
 */
export class Repository {
  //GET Organization Repository
  public getOrganizationRepository(): OrganizationInterface {
    return DB_DRIVER === "couchdb" ? new OrganizationRepository() : new OrganizationRepositoryMongo()
  }
  //GET Researcher Repository
  public getResearcherRepository(): ResearcherInterface {
    return DB_DRIVER === "couchdb" ? new ResearcherRepository() : new ResearcherRepositoryMongo()
  }
  //GET Study Repository
  public getStudyRepository(): StudyInterface {
    return DB_DRIVER === "couchdb" ? new StudyRepository() : new StudyRepositoryMongo()
  }
  //GET ProjectUser
  public getProjectUserRepository(): ProjectUserInterface {
    return DB_DRIVER === "couchdb" ? new ProjectUserRepository() : new ProjectUserRepositoryMongo()
  }
  //GET ProjectRole
  public getProjectRoleRepository(): ProjectRoleInterface {
    return DB_DRIVER === "couchdb" ? new ProjectRoleRepository() : new ProjectRoleRepositoryMongo()
  }
  //GET Participant Repository
  public getParticipantRepository(): ParticipantInterface {
    return DB_DRIVER === "couchdb" ? new ParticipantRepository() : new ParticipantRepositoryMongo()
  }
  //GET Activity Repository
  public getActivityRepository(): ActivityInterface {
    return DB_DRIVER === "couchdb" ? new ActivityRepository() : new ActivityRepositoryMongo()
  }
  //GET Activity Repository
  public getSensorRepository(): SensorInterface {
    return DB_DRIVER === "couchdb" ? new SensorRepository() : new SensorRepositoryMongo()
  }
  //GET ActivityEvent Repository
  public getActivityEventRepository(): ActivityEventInterface {
    return DB_DRIVER === "couchdb" ? new ActivityEventRepository() : new ActivityEventRepositoryMongo()
  }
  //GET ActivityEvent Repository
  public getTriggerRepository(): TriggerInterface {
    return DB_DRIVER === "couchdb" ? new TriggerRepository() : new TriggerRepositoryMongo()
  }
  //GET ActivityEvent Repository
  public getTriggerGroupRepository(): TriggerGroupInterface {
    return DB_DRIVER === "couchdb" ? new TriggerGroupRepository() : new TriggerGroupRepositoryMongo()
  }

  //GET SensorEvent Repository
  public getSensorEventRepository(): SensorEventInterface {
    return DB_DRIVER === "couchdb" ? new SensorEventRepository() : new SensorEventRepositoryMongo()
  }
  //GET ActivitySpec Repository
  public getActivitySpecRepository(): ActivitySpecInterface {
    return DB_DRIVER === "couchdb" ? new ActivitySpecRepository() : new ActivitySpecRepositoryMongo()
  }

  //GET SensorSpec Repository
  public getSensorSpecRepository(): SensorSpecInterface {
    return DB_DRIVER === "couchdb" ? new SensorSpecRepository() : new SensorSpecRepositoryMongo()
  }

  //GET Credential Repository
  public getCredentialRepository(): CredentialInterface {
    return DB_DRIVER === "couchdb" ? new CredentialRepository() : new CredentialRepositoryMongo()
  }

  //GET TypeRepository Repository
  public getTypeRepository(): TypeInterface {
    return DB_DRIVER === "couchdb" ? new TypeRepository() : new TypeRepositoryMongo()
  }
}

/**
 * Creating singleton class for redis
*/
export class RedisFactory {
  private static instance: ioredis.Redis
  private constructor() {}
  
  /**
   * @returns redis client instance
  */
  public static getInstance(): ioredis.Redis {
    if (this.instance === undefined) {
      this.instance = new ioredis(
                parseInt(`${(process.env.REDIS_HOST as any).match(/([0-9]+)/g)?.[0]}`),
                (process.env.REDIS_HOST as any).match(/\/\/([0-9a-zA-Z._]+)/g)?.[0],
      {
        reconnectOnError() {
          return 1
        },
        enableReadyCheck: true,
      })
    }
    return this.instance
  }
}