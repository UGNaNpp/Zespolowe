import Devices from '@/app/components/devices/Devices';
import { getDictionary } from "@/app/[lang]/dictionaries";
import NavBar from '@/app/components/navbar/NavBar';
import styles from '@/app/[lang]/devices/devicesPageStyle.module.scss';

export async function generateMetadata({ params }: { params: { lang: 'en' | 'pl' } }): Promise<Metadata> {
  const resolvedParams = await params;
  const dict = await getDictionary(resolvedParams.lang);

  return {
    title: dict.devices.pageTitle
  };
}

export default async function LoginPage({ params }: { params: Promise<{ lang: 'en' | 'pl' }> }) {
  const { lang } = await params
  const dict = await getDictionary(lang)

  return(
    <main className={styles.main}>
      <NavBar title='Devices' titleUrl='/devices' subtitle='' subtitleUrl='' dict={dict.navBar} />
      <Devices dict={dict.devices} ApiErrorsDict={dict.apiErrors} />
    </main>
  );
}
