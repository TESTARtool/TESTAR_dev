import { Page } from "./Page";
import { Iteration } from "./Iteration";
import { Links } from "./Links";

export interface IterationsDto {
  page: Page
  _embedded: {
    iterations: Iteration[],
  },
  _links: Links,
}
