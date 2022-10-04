export interface Iteration {
  id: number,
  info: string,
  severity: number,
  reportId: number,
  actionIds: number[],
  lastExecutedActionId: number,
  lastStateId: number,
}
