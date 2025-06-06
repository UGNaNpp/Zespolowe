import NavBar from '@/app/components/navbar/NavBar';
import Dashboard from '@/app/components/dashboard/Dashboard';
import styles from '@/app/[lang]/dashboard/dashboardPageStyle.module.scss';
import { getDictionary } from "@/app/[lang]/dictionaries";

export async function generateMetadata({ params }: { params: { lang: 'en' | 'pl' } }): Promise<Metadata> {
  const resolvedParams = await params;
  const dict = await getDictionary(resolvedParams.lang);

  return {
    title: dict.dashboard.pageTitle
  };
}

export default async function DashboardPage({ params }: { params: Promise<{ lang: 'en' | 'pl' }> }) {
  const { lang } = await params
  const dict = await getDictionary(lang)

  return (
    <main className={styles.main}>
      <NavBar title='Dashboard' titleUrl='/dashboard' subtitle='' subtitleUrl='' dict={dict.navBar} />
      <Dashboard dict={dict.dashboard} />
    </main>
  );
}
