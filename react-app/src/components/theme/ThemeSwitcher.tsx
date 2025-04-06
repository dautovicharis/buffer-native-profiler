import { useState } from 'react';

/**
 * ThemeSwitcher - Toggles between light and dark themes
 * - Maintains theme state in localStorage
 * - Updates document theme attribute
 * - Dispatches events for other components
 */
const ThemeSwitcher = () => {
  const [theme, setTheme] = useState(() => {
    return localStorage.getItem('theme') || 'light';
  });


  // Toggle between light and dark themes
  const toggleTheme = () => {
    const newTheme = theme === 'light' ? 'dark' : 'light';
    setTheme(newTheme);
    localStorage.setItem('theme', newTheme);

    // Dispatch event for ThemeInitializer and other components
    document.dispatchEvent(
      new CustomEvent('themeChanged', { detail: { theme: newTheme } })
    );
  };

  return (
    <div className="relative">
      <button
        className="bg-none border-none cursor-pointer text-xl p-2 rounded-full w-10 h-10 flex items-center justify-center bg-light dark:bg-dark shadow-light dark:shadow-dark transition-transform duration-300 hover:translate-y-[-2px] hover:shadow-medium dark:hover:shadow-dark-medium active:translate-y-0"
        aria-label="Toggle dark mode"
        onClick={toggleTheme}
      >
        <span className="dark:hidden">☀️</span>
        <span className="hidden dark:inline">🌙</span>
      </button>
    </div>
  );
};

export default ThemeSwitcher;
