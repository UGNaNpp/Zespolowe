import Login from '@/app/components/login/Login';
import { getDictionary } from "@/app/[lang]/dictionaries";
import type { Metadata } from 'next';

export async function generateMetadata({ params }: { params: { lang: 'en' | 'pl' } }): Promise<Metadata> {
  const resolvedParams = await params;
  const dict = await getDictionary(resolvedParams.lang);

  return {
    title: dict.login.pageTitle
  };
}

export default async function LoginPage({ params }: { params: Promise<{ lang: 'en' | 'pl' }> }) {
  const { lang } = await params
  const dict = await getDictionary(lang)

  return <Login dict={dict.login} />;
}
