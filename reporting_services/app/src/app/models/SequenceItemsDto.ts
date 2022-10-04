import { Links } from "./Links";
import { Page } from "./Page";
import { SequenceItem } from "./SequenceItem";

export interface SequenceItemsDto {
  page: Page,
  _embedded: {
    sequenceItems: SequenceItem[],
  },
  _links: Links,
}
