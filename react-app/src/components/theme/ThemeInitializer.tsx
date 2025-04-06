import { useEffect, useState } from 'react';

// Define the custom event type
interface ThemeChangedEvent extends CustomEvent {
  detail: { theme: string };
}

// Declare the custom event to extend DocumentEventMap
declare global {
  interface DocumentEventMap {
    'themeChanged': ThemeChangedEvent;
  }
}

/**
 * ThemeInitializer - Ensures theme consistency throughout the app lifecycle
 * - Applies theme from localStorage on mount
 * - Listens for theme changes from other components
 * - Handles theme changes from other browser tabs
 * - Prevents flash of incorrect theme with script in index.html
 */
const ThemeInitializer = () => {
  // Track theme in component state
  const [theme, setTheme] = useState(() => {
    // This is already set by the script in index.html, but we need it for state
    return localStorage.getItem('theme') || 'light';
  });

  useEffect(() => {
    // Ensure theme is applied
    document.documentElement.setAttribute('data-theme', theme);

    // Apply Tailwind dark mode class
    if (theme === 'dark') {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }

    // Event handlers
    const handleThemeChange = (e: ThemeChangedEvent) => {
      if (e.detail?.theme) setTheme(e.detail.theme);
    };

    const handleStorageChange = (e: StorageEvent) => {
      if (e.key === 'theme' && e.newValue !== theme) {
        setTheme(e.newValue || 'light');
      }
    };

    // Add event listeners
    document.addEventListener('themeChanged', handleThemeChange);
    window.addEventListener('storage', handleStorageChange);

    return () => {
      document.removeEventListener('themeChanged', handleThemeChange);
      window.removeEventListener('storage', handleStorageChange);
    };
  }, [theme]);

  return null;
};

export default ThemeInitializer;
