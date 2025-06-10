import Stream from "@/app/components/stream/Stream";
import { getDictionary } from "@/app/[lang]/dictionaries";
import NavBar from "@/app/components/navbar/NavBar";
import styles from '@/app/[lang]/stream/[deviceId]/streamPageStyle.module.scss';
import type { Metadata } from 'next';
import type { Lang } from '@/types/routeParams';

type StreamParams = {
  lang: Lang;
  deviceId: string;
};

export type StreamProps = {
  params: Promise<StreamParams>;
};

export async function generateMetadata({ params }: StreamProps): Promise<Metadata> {
  const resolvedParams = await params;
  const dict = await getDictionary(resolvedParams.lang);

  return {
    title: dict.stream.pageTitle
  };
}

export default async function StreamPage({ params }: StreamProps) {
  const { lang, deviceId } = await params;
  const dict = await getDictionary(lang)

  return(
    <main className={styles.main}>
      <NavBar title={`Device ${deviceId}`} titleUrl={`/device/${deviceId}`} subtitle='stream' subtitleUrl={`/stream/${deviceId}`} dict={dict.navBar} />
      <Stream deviceId={deviceId} dict={dict.stream} ApiErrorsDict={dict.apiErrors}/>
    </main>
  );
}
