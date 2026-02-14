# Holoseogi Backend Showcase

이 레포지토리는 개인 및 앱스토어 출시 프로젝트 **홀로서기** 에서 사용된 백엔드 구조 일부를 정리한 코드입니다.

전체 서비스 코드를 공개하는 목적이 아닌, 실제 프로젝트에서 사용된 백엔드 아키텍처 흐름과 코드 스타일을 공유하기 위한 Showcase 버전입니다.

데이터베이스 Entity, Repository, 민감한 설정 정보 및 일부 비즈니스 로직은 제거되어 있으며,
실행 가능한 완전한 서비스 형태가 아닌 구조 중심의 예시 코드로 구성되어 있습니다.

---

## 사용 기술

- Java
- Spring Boot
- REST API Architecture
- JWT 기반 인증 구조
- Interceptor 기반 요청 흐름 제어
- Scheduler 구성
- Firebase FCM 알림 구조
- OpenAI LLM 연동 예시

---

## 아키텍처 개요

본 레포지토리는 다음과 같은 백엔드 구조 일부를 중심으로 구성되어 있습니다.

- 자체 JWT 발급 및 인증 인터셉터 흐름
- 요청 트래픽 제어를 위한 인터셉터 구성
- Global Exception 처리
- Scheduler 기반 백그라운드 처리 구조
   - Fire base message 전송
   - Appstore 정기 구독 체킹
- 외부 서비스(OpenAI, FCM) 연동 구조 예시

---

## 포함된 구조 요소 (Showcase 범위)

- FCM 기반 알림 처리 흐름 일부
- OpenAI LLM API 연동 예시 코드
- 자체 JWT 토큰 발급 및 인증 처리 구조
- API 요청 인터셉트 및 트래픽 흐름 제어 구조

---

## 참고 사항

본 코드는 실행 가능한 완전한 서비스가 아닌, 구조 중심의 코드임을 알려드립니다.
백엔드 설계 방식과 구조 공유를 위한 코드 입니다.
