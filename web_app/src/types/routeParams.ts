export type Lang = 'en' | 'pl';

export type Params = {
  lang: Lang;
};

export type Props = {
  params: Promise<Params>;
};
