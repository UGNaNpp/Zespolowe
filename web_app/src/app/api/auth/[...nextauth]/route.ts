import NextAuth from "next-auth"
import GithubProvider from "next-auth/providers/github"

const handler = NextAuth({
  providers: [
    GithubProvider({
      clientId: process.env.GITHUB_CLIENT_ID!,
      clientSecret: process.env.GITHUB_CLIENT_SECRET!,
    }),
  ],
  session: {
    strategy: "jwt",
  },
  callbacks: {
    async jwt({ token, account }) {
      if (account) {
        token.accessToken = account.access_token;
        console.log(token)

        token.id = account.id;
      }
      return token;
    },
    async session({ session, token }) {

      console.log('Token:', token);

      console.log('User ID:', token.sub);

      console.log('User Info:', session.user);

      session.user.id = token.id;
      return session;
    },
  },
  secret: process.env.NEXTAUTH_SECRET,
  cookies: {
    sessionToken: {
      name: `next-auth.session-token-backend`,
      options: {
        httpOnly: true,
        secure: process.env.NODE_ENV === "production",
        sameSite: "lax",
        path: "/",
        domain: "http://localhost:8080"
      },
    },
  },
});

export { handler as GET, handler as POST }