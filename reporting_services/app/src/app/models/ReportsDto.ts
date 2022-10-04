import { Links } from "./Links";
import { Page } from "./Page";
import { Report } from "./Report";

export interface ReportsDto {
  page: Page,
  _embedded: {
    reports: Report[],
  },
  _links: Links,
}
