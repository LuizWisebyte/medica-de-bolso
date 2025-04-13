import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useAuth } from '../contexts/AuthContext';
import { FiMenu, FiX, FiUser, FiLogOut } from 'react-icons/fi';

export default function Navbar() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const { user, signOut } = useAuth();
  const router = useRouter();

  const handleSignOut = () => {
    signOut();
    router.push('/');
  };

  return (
    <nav className="bg-white shadow">
      <div className="container mx-auto px-4">
        <div className="flex justify-between h-16">
          <div className="flex">
            <Link href="/dashboard" className="flex items-center">
              <span className="text-xl font-bold text-blue-600">Medica de Bolso</span>
            </Link>
          </div>

          {/* Menu para desktop */}
          <div className="hidden md:flex md:items-center md:space-x-4">
            <Link
              href="/dashboard"
              className={`px-3 py-2 rounded-md text-sm font-medium ${
                router.pathname === '/dashboard'
                  ? 'text-blue-600 bg-blue-50'
                  : 'text-gray-700 hover:text-blue-600 hover:bg-blue-50'
              }`}
            >
              Dashboard
            </Link>
            <Link
              href="/medicamentos"
              className={`px-3 py-2 rounded-md text-sm font-medium ${
                router.pathname === '/medicamentos'
                  ? 'text-blue-600 bg-blue-50'
                  : 'text-gray-700 hover:text-blue-600 hover:bg-blue-50'
              }`}
            >
              Medicamentos
            </Link>
            
            <div className="relative ml-3">
              <div className="flex items-center space-x-4">
                <span className="text-sm font-medium text-gray-700">
                  Olá, {user?.name}
                </span>
                <button
                  onClick={handleSignOut}
                  className="p-2 rounded-full text-gray-700 hover:text-blue-600 hover:bg-blue-50"
                >
                  <FiLogOut className="h-5 w-5" />
                </button>
              </div>
            </div>
          </div>

          {/* Botão do menu mobile */}
          <div className="flex items-center md:hidden">
            <button
              onClick={() => setIsMenuOpen(!isMenuOpen)}
              className="p-2 rounded-md text-gray-700 hover:text-blue-600 hover:bg-blue-50"
            >
              {isMenuOpen ? (
                <FiX className="h-6 w-6" />
              ) : (
                <FiMenu className="h-6 w-6" />
              )}
            </button>
          </div>
        </div>
      </div>

      {/* Menu mobile */}
      {isMenuOpen && (
        <div className="md:hidden">
          <div className="px-2 pt-2 pb-3 space-y-1">
            <Link
              href="/dashboard"
              className={`block px-3 py-2 rounded-md text-base font-medium ${
                router.pathname === '/dashboard'
                  ? 'text-blue-600 bg-blue-50'
                  : 'text-gray-700 hover:text-blue-600 hover:bg-blue-50'
              }`}
            >
              Dashboard
            </Link>
            <Link
              href="/medicamentos"
              className={`block px-3 py-2 rounded-md text-base font-medium ${
                router.pathname === '/medicamentos'
                  ? 'text-blue-600 bg-blue-50'
                  : 'text-gray-700 hover:text-blue-600 hover:bg-blue-50'
              }`}
            >
              Medicamentos
            </Link>
            
            <div className="pt-4 pb-3 border-t border-gray-200">
              <div className="px-3">
                <p className="text-base font-medium text-gray-700">
                  {user?.name}
                </p>
                <p className="text-sm font-medium text-gray-500">
                  {user?.email}
                </p>
              </div>
              <div className="mt-3 px-2">
                <button
                  onClick={handleSignOut}
                  className="block w-full px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:text-blue-600 hover:bg-blue-50"
                >
                  Sair
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </nav>
  );
} 