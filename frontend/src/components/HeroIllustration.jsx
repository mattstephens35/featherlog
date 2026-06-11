export default function HeroIllustration() {
  return (
    <svg
      className="hero__illustration"
      viewBox="0 0 400 300"
      xmlns="http://www.w3.org/2000/svg"
      role="img"
      aria-label="Illustration of birds flying over hills and a lake at sunrise"
    >
      <defs>
        <linearGradient id="hero-sky" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#fdf6e3" />
          <stop offset="100%" stopColor="#cfe8e0" />
        </linearGradient>
        <linearGradient id="hero-hill-far" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#3f9c74" />
          <stop offset="100%" stopColor="#2e7d5b" />
        </linearGradient>
        <linearGradient id="hero-hill-near" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#1f6f5c" />
          <stop offset="100%" stopColor="#175c4c" />
        </linearGradient>
        <linearGradient id="hero-water" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#bcdcef" />
          <stop offset="100%" stopColor="#9cc9e6" />
        </linearGradient>
      </defs>

      <rect width="400" height="300" fill="url(#hero-sky)" />
      <circle cx="320" cy="60" r="36" fill="#e0a458" opacity="0.85" />

      <path d="M0 190 Q80 130 180 175 T400 160 V300 H0 Z" fill="url(#hero-hill-far)" opacity="0.55" />
      <path d="M0 220 Q100 160 220 210 T400 200 V300 H0 Z" fill="url(#hero-hill-near)" />

      <path d="M0 255 Q100 240 200 258 T400 250 V300 H0 Z" fill="url(#hero-water)" />

      <g transform="translate(60 165)">
        <rect x="-3" y="20" width="6" height="34" fill="#7a5230" />
        <ellipse cx="0" cy="10" rx="26" ry="24" fill="#1f6f5c" />
        <ellipse cx="-14" cy="22" rx="18" ry="16" fill="#2e7d5b" />
        <ellipse cx="16" cy="20" rx="18" ry="16" fill="#2e7d5b" />
      </g>

      <g stroke="#1f2937" strokeWidth="3" strokeLinecap="round" fill="none" opacity="0.75">
        <path d="M150 70 q8 -10 16 0 q8 -10 16 0" />
        <path d="M210 100 q7 -9 14 0 q7 -9 14 0" />
        <path d="M255 55 q6 -8 12 0 q6 -8 12 0" />
        <path d="M180 130 q6 -8 12 0 q6 -8 12 0" />
      </g>
    </svg>
  );
}
