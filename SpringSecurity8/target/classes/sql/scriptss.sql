create table users(username varchar(50) not null primary key,password varchar(500) not null ,enabled boolean not null);
create table authorities(username varchar(50) not null, authority varchar(50) not null,constraint fk_authorities_users foreign key(username)references users (username));
create unique index ix_auth_username on authorities (username, authority);


 \resumeSubHeadingListStart
     \resumeProjectHeading
          {\href{#}{\textbf{\large{\underline{JWT Authentication}}} \href{Project Link}{\raisebox{-0.1\height}\faExternalLink }} $|$ \large{\underline{Java, Spring Boot, Spring Security, JWT, JPA, PostgreSQL/MySQL}}}{2023 - 24}\\
          \resumeItemListStart
            \resumeItem {\normalsize{Implemented User registration, login, role-based authorization}}
            \resumeItem{\normalsize{JWT-based authentication with refresh token support}}
            \resumeItem{\normalsize{Secure RESTful APIs using Spring Security}}
            \resumeItem{\normalsize{JWT Structure: Header (token type & signing algorithm), Payload (user claims), Signature (verifies integrity)}}
            \resumeItem{\normalsize{Database integration with PostgreSQL/MySQL}}
            
            
          \resumeItemListEnd 