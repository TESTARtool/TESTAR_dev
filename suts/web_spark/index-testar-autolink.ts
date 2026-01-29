import { Router } from "express"
import { QueryAPI } from "./Query"
import { ListenerAPI } from "../utils/ListenerAPI"
import { PushNotificationAPI } from "../utils/PushNotificationAPI"

import { TypeService } from "./TypeService"
import { CredentialService } from "./CredentialService"
import { OrganizationService } from "./OrganizationService"
import { ResearcherService } from "./ResearcherService"
import { ProjectRoleService } from "./ProjectRoleService"
import { StudyService } from "./StudyService"
import { ParticipantService } from "./ParticipantService"
import { ActivityService } from "./ActivityService"
import { ActivitySpecService } from "./ActivitySpecService"
import { ActivityEventService } from "./ActivityEventService"
import { TriggerService } from "./TriggerService"
import { SensorService } from "./SensorService"
import { SensorSpecService } from "./SensorSpecService"
import { SensorEventService } from "./SensorEventService"
import { TestFixtureService } from "./TestFixtureService"

export { TypeService } from "./TypeService"
export { CredentialService } from "./CredentialService"
export { OrganizationService } from "./OrganizationService"
export { ResearcherService } from "./ResearcherService"
export { ProjectRoleService } from "./ProjectRoleService"
export { StudyService } from "./StudyService"
export { ParticipantService } from "./ParticipantService"
export { ActivityService } from "./ActivityService"
export { ActivitySpecService } from "./ActivitySpecService"
export { ActivityEventService } from "./ActivityEventService"
export { TriggerService } from "./TriggerService"
export { SensorService } from "./SensorService"
export { SensorSpecService } from "./SensorSpecService"
export { SensorEventService } from "./SensorEventService"
export { TestFixtureService } from "./TestFixtureService"

const API = Router()
API.use(TypeService.Router)
API.use(CredentialService.Router)
API.use(OrganizationService.Router)
API.use(ResearcherService.Router)
API.use(ProjectRoleService.Router)
API.use(StudyService.Router)
API.use(ParticipantService.Router)
API.use(ActivityService.Router)
API.use(ActivitySpecService.Router)
API.use(ActivityEventService.Router)
API.use(TriggerService.Router)
API.use(SensorService.Router)
API.use(SensorSpecService.Router)
API.use(SensorEventService.Router)
API.use(TestFixtureService.Router)
API.use(QueryAPI)
API.use("/subscribe", ListenerAPI)
API.use("/send", PushNotificationAPI)
export default API
