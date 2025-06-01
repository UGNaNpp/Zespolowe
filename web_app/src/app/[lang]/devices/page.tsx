import Devices from '../../components/devices/Devices';
import { getDictionary } from "../dictionaries";
import NavBar from '../../components/navbar/NavBar';
import styles from './devicesPageStyle.module.scss';

export default async function LoginPage({ params }: { params: Promise<{ lang: 'en' | 'pl' }> }) {
  const { lang } = await params
  const dict = await getDictionary(lang)

  return(
    <main className={styles.main}>
      <NavBar title='Devices' titleUrl='/devices' subtitle='' subtitleUrl='' />
      <Devices dict={dict.devices} />
    </main>
  );
}
