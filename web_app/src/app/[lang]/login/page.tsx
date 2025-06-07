import Login from '@/app/components/login/Login';
import { getDictionary } from "@/app/[lang]/dictionaries";
import type { Metadata } from 'next';
import type { Props } from '@/types/routeParams';

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const resolvedParams = await params;
  const dict = await getDictionary(resolvedParams.lang);

  return {
    title: dict.login.pageTitle
  };
}

export default async function LoginPage({ params }: Props) {
  const { lang } = await params
  const dict = await getDictionary(lang)

  return <Login dict={dict.login} />;
}
