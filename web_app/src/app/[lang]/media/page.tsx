import Media from '@/app/components/media/Media';
import { getDictionary } from "@/app/[lang]/dictionaries";
import type { Metadata } from 'next';
import type { Props } from '@/types/routeParams';
import styles from '@/app/[lang]/media/mediaPageStyle.module.scss';
import NavBar from '@/app/components/navbar/NavBar';

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const resolvedParams = await params;
  const dict = await getDictionary(resolvedParams.lang);

  return {
    title: dict.media.pageTitle
  };
}

export default async function MediaPage({ params }: Props) {
  const { lang } = await params
  const dict = await getDictionary(lang)

    return(
    <main className={styles.main}>
      <NavBar title='Devices' titleUrl='/devices' subtitle='' subtitleUrl='' dict={dict.navBar} />
      <Media dict={dict.media} ApiErrorsDict={dict.apiErrors} />
    </main>
  );
}
