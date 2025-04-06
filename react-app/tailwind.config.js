/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // Light theme colors
        primary: {
          DEFAULT: '#2196F3',
          dark: '#90CAF9',
        },
        success: {
          DEFAULT: '#4CAF50',
          dark: '#81C784',
        },
        warning: {
          DEFAULT: '#FFC107',
          dark: '#FFD54F',
        },
        danger: {
          DEFAULT: '#F44336',
          dark: '#E57373',
        },
        accent: {
          DEFAULT: '#FF9800',
          dark: '#FFB74D',
        },
        // Buffer type colors
        'string-buffer': {
          DEFAULT: '#4CAF50',
          dark: '#66BB6A',
        },
        'bytearray-buffer': {
          DEFAULT: '#2196F3',
          dark: '#42A5F5',
        },
        'int-buffer': {
          DEFAULT: '#9C27B0',
          dark: '#AB47BC',
        },
      },
      backgroundColor: {
        light: '#f5f5f5',
        dark: '#121212',
        'card-light': '#ffffff',
        'card-dark': '#1E1E1E',
      },
      textColor: {
        light: '#333',
        'light-secondary': '#757575',
        'light-muted': '#666',
        dark: '#E0E0E0',
        'dark-secondary': '#BBBBBB',
        'dark-muted': '#AAAAAA',
      },
      borderRadius: {
        'sm': '4px',
        'md': '8px',
        'lg': '12px',
      },
      boxShadow: {
        'light': '0 2px 4px rgba(0,0,0,0.1)',
        'medium': '0 4px 8px rgba(0,0,0,0.1)',
        'dark': '0 2px 4px rgba(0,0,0,0.5)',
        'dark-medium': '0 4px 8px rgba(0,0,0,0.5)',
      },
      borderColor: {
        'light': 'rgba(0,0,0,0.1)',
        'dark': 'rgba(255,255,255,0.1)',
      },
    },
  },
  plugins: [],
  darkMode: 'class',
}
