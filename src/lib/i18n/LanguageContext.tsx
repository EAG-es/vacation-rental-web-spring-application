import React, { createContext, useState, useContext, useEffect } from 'react';
import { translations } from './translations';

type LanguageContextType = {
  language: string;
  setLanguage: (lang: string) => void;
  t: (key: string) => string;
};

const defaultLanguage = 'en';

const LanguageContext = createContext<LanguageContextType>({
  language: defaultLanguage,
  setLanguage: () => {},
  t: (key: string) => key,
});

export const useLanguage = () => useContext(LanguageContext);

export const LanguageProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [language, setLanguageState] = useState<string>(() => {
    // Try to get the language from localStorage, default to 'en'
    return localStorage.getItem('preferredLanguage') || defaultLanguage;
  });

  const setLanguage = (lang: string) => {
    localStorage.setItem('preferredLanguage', lang);
    setLanguageState(lang);
    // Update HTML lang attribute for accessibility
    document.documentElement.lang = lang;
  };

  // Translation function
  const t = (key: string): string => {
    const keys = key.split('.');
    let translation: any = translations[language];
    
    for (const k of keys) {
      if (!translation[k]) {
        // Fallback to English if translation not found
        translation = translations[defaultLanguage];
        for (const fallbackKey of keys) {
          translation = translation[fallbackKey];
          if (!translation) break;
        }
        break;
      }
      translation = translation[k];
    }
    
    return translation || key; // Return the key itself if no translation found
  };

  useEffect(() => {
    // Set the HTML lang attribute when the component mounts
    document.documentElement.lang = language;
  }, [language]);

  return (
    <LanguageContext.Provider value={{ language, setLanguage, t }}>
      {children}
    </LanguageContext.Provider>
  );
};