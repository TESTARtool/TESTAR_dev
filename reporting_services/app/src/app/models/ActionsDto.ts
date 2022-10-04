import { Action } from "./Action";
import { Links } from "./Links";
import { Page } from "./Page";

export interface ActionsDto {
  page: Page,
  _embedded: {
    actions: Action[],
  },
  _links: Links,
}
