# 허니도그 AI챗 서버

"허니도그AI챗"은 사용자들이 가상의 도시 '허니도그 시티'의 주민 AI 캐릭터들과 대화를 나눌 수 있는 채팅 어플리케이션입니다. 
각 AI 캐릭터는 동물의 모습을 하고 있으며, 자신만의 취미, 개성, 전문 지식 등을 가지고 있어 다양한 주제에 대해 대화를 나눌 수 있습니다.
다양한 AI 캐릭터들과의 대화를 통해 사용자들에게 색다른 채팅 경험을 제공하며, 사용자들은 새로운 정보를 얻을 수 있고, 재미있는 이야기를 공유하며, 자신만의 AI를 만들고 공유함으로써 창의적인 활동을 할 수 있습니다. 
또한, 사용자들은 자신의 관심사나 궁금한 사항에 대해 AI 캐릭터들과 대화할 수 있고, 자신만의 AI 캐릭터를 생성하여 앱 내에서 다른 사용자들과 대화할 수도 있습니다. 

앱은 현재 안드로이드 비공개테스트중입니다.

허니도그AI챗 앱의 백엔드 서버 부분입니다.
허니도그AI챗에 필요한 유저, 채팅방, 채팅 관련 내용이 있으며, 관리를 위한 페이지도 일부 있습니다.
AI와 채팅은 OpenAI의 Assistants Api의 내용을 사용하였습니다.

원본 서버를 복사한 서버로, 일부 올라와서는 안되는 개인정보와 enum, 보안관련 내용이 삭제되어 있습니다.
이전하기전 travisCi를 사용했고, 현재 깃랩CICD를 사용하기 때문에 CICD 파일이 있습니다.

|   |   |   |
|---|---|---|
| <img src="https://honeydog.co.kr/images/web/Screenshot_1711779242.png" alt="스크린샷 1" width="300" height="600"> | <img src="https://honeydog.co.kr/images/web/Screenshot_1711779254.png" alt="스크린샷 2" width="300" height="600"> | <img src="https://honeydog.co.kr/images/web/Screenshot_1715758692.png" alt="스크린샷 3" width="300" height="600"> |

## 시작하기 & 사전 조건

1. application-local.properties, application-oauth.properties 파일 생성
2. application-local.properties는아래와 같이 작성
spring.jpa.hibernate.ddl-auto=none
spring.datasource.url={로컬db url}
spring.datasource.username={로컬db 유저네임}
spring.datasource.password={로컬db 비번}
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

#openAi token
openai.token={openAI토큰}

3. application-oauth.propertes는 아래와 같이 작성
spring.security.oauth2.client.registration.google.client-id={구글 클라이언트 아이디}
spring.security.oauth2.client.registration.google.client-secret={구글 클라이언트 시크릿}
spring.security.oauth2.client.registration.google.scope=profile,email

#registration
spring.security.oauth2.client.registration.naver.client-id={네이버 클라이언트 아이디}
spring.security.oauth2.client.registration.naver.client-secret={네이버 클라이언트 시크릿}
spring.security.oauth2.client.registration.naver.redirect-uri={baseUrl}/{action}/oauth2/code/{registrationId}
spring.security.oauth2.client.registration.naver.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.naver.scope=name,email,profile_image
spring.security.oauth2.client.registration.naver.client-name=Naver

#provider
spring.security.oauth2.client.provider.naver.authorization-uri=https://nid.naver.com/oauth2.0/authorize
spring.security.oauth2.client.provider.naver.token-uri=https://nid.naver.com/oauth2.0/token
spring.security.oauth2.client.provider.naver.user-info-uri=https://openapi.naver.com/v1/nid/me
spring.security.oauth2.client.provider.naver.user-name-attribute=response

구글/네이버 클라이언트 아이디와, 시크릿을 각자 생성 후 설정
구글 클라우드 플랫폼 주소(https://console.cloud.google.com)
네이버 API등록(https://developers.naver.com/apps/#/register?api=nvlogin)

그외 비어있는 부분(enum, config...) 확인 필요

## 개발 환경 설정
JDK: JDK 17
DB: MariaDB

## 연락처 정보

kjy48048@gmail.com
