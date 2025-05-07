import Login from '../../components/login/Login';
import { getDictionary } from "../dictionaries";

export default async function LoginPage({ params }: { params: Promise<{ lang: 'en' | 'pl' }> }) {
  const { lang } = await params
  const dict = await getDictionary(lang)

  return <Login dict={dict.login} />;
}
