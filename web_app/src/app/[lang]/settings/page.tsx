import NavBar from '@/app/components/navbar/NavBar';
import Settings from '@/app/components/settings/SettingsMain';
import styles from '@/app/[lang]/dashboard/dashboardPageStyle.module.scss';
import { getDictionary } from "@/app/[lang]/dictionaries";
import type { Props } from '@/types/routeParams';
import type { Metadata } from 'next';


export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const resolvedParams = await params;
  const dict = await getDictionary(resolvedParams.lang);

  return {
    title: dict.dashboard.pageTitle
  };
}

export default async function SettingsPage({ params }: Props) {
  const { lang } = await params
  const dict = await getDictionary(lang)

  return (
    <main className={styles.main}>
      <NavBar title='Settings' titleUrl='/settings' subtitle='' subtitleUrl='' dict={dict.navBar} />
      <Settings />
    </main>
  );
}
