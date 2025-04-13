/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  images: {
    domains: [],
  },
  // Configurações para garantir que o Tailwind funcione corretamente
  webpack: (config) => {
    return config;
  },
  async redirects() {
    return [
      {
        source: '/register',
        destination: '/registro',
        permanent: true,
      },
      {
        source: '/forgot-password',
        destination: '/recuperar-senha',
        permanent: true,
      },
    ];
  },
}

module.exports = nextConfig 