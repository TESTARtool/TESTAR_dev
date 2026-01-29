import { Request, Response, Router } from "express"
import { ApiResponseHeaders, Bootstrap, Encrypt, MongoClientDB, uuid } from "../repository/Bootstrap"

export class TestFixtureService {
  public static Router = Router()
}

async function ensureMongo() {
  await Bootstrap()
  if (!MongoClientDB) {
    throw new Error("400.mongo-not-configured")
  }
}

function assertTestOnly() {
  if (process.env.NODE_ENV !== "test") {
    throw new Error("404.not-found")
  }
}

type Scenario = "empty" | "project" | "survey"

const scenarios: Record<Scenario, {
  organizationName: string
  researcherName: string
  accessKey: string
  password: string
  projectName?: string
  createSurvey: boolean
}> = {
  empty: {
    organizationName: "Empty organization",
    researcherName: "EMPTY-User",
    accessKey: "empty-user",
    password: "EmptyPass1!",
    createSurvey: false,
  },
  project: {
    organizationName: "Project organization",
    researcherName: "PROJECT-User",
    accessKey: "project-user",
    password: "ProjectPass1!",
    projectName: "Sample Project",
    createSurvey: false,
  },
  survey: {
    organizationName: "Survey organization",
    researcherName: "SURVEY-User",
    accessKey: "survey-user",
    password: "SurveyPass1!",
    projectName: "Sample Project",
    createSurvey: true,
  },
}

async function resetScenario(scenario: Scenario) {
  await ensureMongo()

  const config = scenarios[scenario]
  const { organizationName, researcherName, accessKey, password, projectName, createSurvey } = config

  const organization = await MongoClientDB.collection("organization").findOne({ name: organizationName })
  const organizationId = organization?._id ?? uuid()

  let projectIds: any[] = []
  if (organization?._id) {
    const projectQuery = projectName
      ? { name: projectName, _parent: organization._id }
      : { _parent: organization._id }
    const projects = await MongoClientDB.collection("study").find(projectQuery).toArray()
    projectIds = projects.map((p: any) => p._id)
  }

  if (projectIds.length > 0) {
    await MongoClientDB.collection("activity_event").deleteMany({ _parent: { $in: projectIds } })
    await MongoClientDB.collection("sensor_event").deleteMany({ _parent: { $in: projectIds } })
    await MongoClientDB.collection("sensor").deleteMany({ _parent: { $in: projectIds } })
    await MongoClientDB.collection("participant").deleteMany({ _parent: { $in: projectIds } })
    await MongoClientDB.collection("activity").deleteMany({ _parent: { $in: projectIds } })
    await MongoClientDB.collection("project_user").deleteMany({ project_id: { $in: projectIds } })
    await MongoClientDB.collection("study").deleteMany({ _id: { $in: projectIds } })
  }

  await MongoClientDB.collection("credential").deleteMany({ access_key: accessKey })
  await MongoClientDB.collection("researcher").deleteMany({ name: researcherName })
  await MongoClientDB.collection("organization").deleteMany({ name: organizationName })

  await MongoClientDB.collection("organization").insertOne({
    _id: organizationId,
    name: organizationName,
    language: "en",
    parent_organization: null,
    _deleted: false,
  })

  const researcherId = uuid()
  await MongoClientDB.collection("researcher").insertOne({
    _id: researcherId,
    name: researcherName,
    organization_id: organizationId,
    role: "admin",
    _parent: organizationId,
    _deleted: false,
  })

  await MongoClientDB.collection("credential").insertOne({
    origin: researcherId,
    access_key: accessKey,
    secret_key: Encrypt(password, "AES256"),
    description: researcherName,
    _deleted: false,
  })

  let projectId: string | null = null
  let activityIds: string[] = []

  if (projectName) {
    projectId = uuid()
    await MongoClientDB.collection("study").insertOne({
      _id: projectId,
      _parent: organizationId,
      name: projectName,
      joinable: true,
      _deleted: false,
    })

    if (createSurvey) {
      const activityId = uuid()
      await MongoClientDB.collection("activity").insertOne({
        _id: activityId,
        _parent: projectId,
        timestamp: new Date().getTime(),
        spec: "spark.survey",
        name: "Default Survey",
        settings: [{ text: "This is a default survey.", type: "text", required: true }],
        schedule: [],
        category: ["assess"],
        _deleted: false,
      })
      activityIds = [activityId]
    }
  }

  return {
    scenario,
    organization_id: organizationId,
    researcher_id: researcherId,
    project_id: projectId,
    activity_ids: activityIds,
    access_key: accessKey,
    password,
  }
}

TestFixtureService.Router.post("/test/reset", async (req: Request, res: Response) => {
  res.header(ApiResponseHeaders)
  try {
    assertTestOnly()
    const raw = req.body?.scenario ?? req.body?.user ?? req.body?.fixture ?? req.body?.name
    const key = typeof raw === "string" ? raw.toLowerCase().trim() : ""
    const scenario: Scenario | undefined =
      key.includes("empty") ? "empty" :
      key.includes("project") ? "project" :
      key.includes("survey") ? "survey" :
      undefined;
    if (!scenario) {
      throw new Error("400.missing-scenario")
    }
    const data = await resetScenario(scenario)
    res.json({ data })
  } catch (e: any) {
    res.status(parseInt(e.message?.split(".")[0]) || 500).json({ error: e.message })
  }
})
