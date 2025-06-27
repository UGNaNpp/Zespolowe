import NoAccess from '@/app/components/noaccess/NoAccess';
import { getDictionary } from "@/app/[lang]/dictionaries";
import type { Metadata } from 'next';
import type { Props } from '@/types/routeParams';
import styles from '@/app/[lang]/notfound/notfoundPageStyle.module.scss';

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const resolvedParams = await params;
  const dict = await getDictionary(resolvedParams.lang);

  return {
    title: dict.notfound.pageTitle
  };
}

export default async function NotFoundPage({ params }: Props) {
  const { lang } = await params
  const dict = await getDictionary(lang)

    return(
    <main className={styles.main}>
      <NoAccess dict={dict.notfound} />
    </main>
  );
}
