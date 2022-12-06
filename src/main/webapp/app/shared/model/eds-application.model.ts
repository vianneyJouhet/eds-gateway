export interface IEDSApplication {
  id?: string;
  name?: string | null;
  logoContentType?: string;
  logo?: string;
  link?: string | null;
  description?: string | null;
  category?: string | null;
  authorizedRole?: string | null;
  needAuth?: boolean | null;
  defaultHidden?: boolean | null;
}

export const defaultValue: Readonly<IEDSApplication> = {
  needAuth: false,
  defaultHidden: false,
};
