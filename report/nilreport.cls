%------------------------------------------------------------------------------
% Technical Report template for the Narrative Intelligence Lab
%------------------------------------------------------------------------------
\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{nilreport}

% Use the 'article' class as a base.
\LoadClass[12pt]{article}

% 1.5 inch margins
\usepackage[margin=1.5in,nohead]{geometry}

% Reduce hyphenation.
\pretolerance=5000
\tolerance=9000
\emergencystretch=0pt
\righthyphenmin=4
\lefthyphenmin=4

% Linux Libertine at the main font and Bera Mono as the monospace font.
\usepackage[T1]{fontenc}
\usepackage{libertine}
\usepackage{libertinust1math}
\usepackage[scaled=0.83]{beramono}

% Used to display images.
\usepackage{graphicx}

% Cross-references are hypoerlinks without colored boxed drawns around them.
\usepackage[hidelinks]{hyperref}

% Bibliogrpahy package.
\usepackage{natbib}

% Add the References section to the Table of Contents.
\usepackage[nottoc]{tocbibind}

% Define a custom macro for the abstract so it can appear on the title page.
\def\@abstract{}
\renewcommand{\abstract}[1]{%
  \long\def\@abstract{%
    #1
  }%
}
% Custom title page.
\renewcommand{\maketitle}{%
	\hypersetup{pageanchor=false}
	\begin{titlepage}
		\begin{center}
			\vspace*{1cm}
			{\huge \@title \par}
			\vspace{0.5cm}
			{\Large Technical Report \par}
			\vspace{0.5cm}
			{\@date \par}
			\vspace{0.5cm}
			{\large
				\@author \par
				Narrative Intelligence Lab\\
				Department of Computer Science\\
				University of Kentucky\par
			}
			\vspace{0.5cm}
			\includegraphics[width=5cm]{nillogo}
			\raisebox{0.25\height}{\includegraphics[width=5cm]{uklogo}}\par
			\vspace{1cm}
			{\large Abstract \par}
		\end{center}
		{\@abstract \par}
	\end{titlepage}
	\hypersetup{pageanchor=true}
}