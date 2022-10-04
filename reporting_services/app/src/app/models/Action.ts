export interface Action {
  id: number,
  description: string,
  status: string,
  screenshot: string,
  selected: boolean,
  startTime: string,
  widgetPath: string,
  iterationId: number,
  sequenceItemId: number,
}
