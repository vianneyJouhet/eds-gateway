import { IA } from 'app/shared/model/a.model';

export interface IB {
  id?: number;
  a?: IA | null;
}

export const defaultValue: Readonly<IB> = {};
