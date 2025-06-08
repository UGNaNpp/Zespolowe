import NoAccess from '@/app/components/noaccess/NoAccess';
import { getDictionary } from "@/app/[lang]/dictionaries";
import type { Metadata } from 'next';
import type { Props } from '@/types/routeParams';
import styles from '@/app/[lang]/noaccess/noaccessPageStyle.module.scss';

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const resolvedParams = await params;
  const dict = await getDictionary(resolvedParams.lang);

  return {
    title: dict.noaccess.pageTitle
  };
}

export default async function NoAccessPage({ params }: Props) {
  const { lang } = await params
  const dict = await getDictionary(lang)

    return(
    <main className={styles.main}>
      <NoAccess dict={dict.noaccess} />
    </main>
  );
}
