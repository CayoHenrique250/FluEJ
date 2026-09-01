# FluEJ

O projeto nasce para solucionar uma das maiores vulnerabilidades do Movimento Empresa Júnior (MEJ): a perda contínua de conhecimento organizacional provocada pela alta rotatividade de membros e gestores. Em decorrência do ciclo universitário, a transição constante de equipes faz com que processos documentados, KPIs e históricos de projetos se percam em repasses informais. Para mitigar esse problema, a proposta consiste no desenvolvimento de uma plataforma e guia interativo de gestão inicial. A ferramenta atua na manutenção dos padrões de qualidade e organização da EJ ao longo do tempo, permitindo o acompanhamento do ciclo de produção dos membros, gestão de onboarding e offboarding, emissão de relatórios estratégicos e a consolidação de uma base de conhecimento de longo prazo.

---

## Pré-requisitos de Ambiente

Antes de começar, certifique-se de ter os seguintes softwares instalados na sua máquina:

* **Git**
* **JDK 25**
* **Android SDK** (API 37 / Min SDK 24)
* **IDE:** [Android Studio](https://developer.android.com/studio) ou [VS Code](https://code.visualstudio.com/)

---

## 1. Clonando o Repositório

```bash
git clone https://github.com/CayoHenrique250/FluEJ

cd FluEJ
```

## 2. Como Rodar o Projeto

**Opção A: Android Studio**

1. Abra o Android Studio e selecione **Open**.
2. Navegue até a pasta do projeto e confirme.
3. Aguarde o **Gradle Sync** finalizar.
4. Verifique em `Settings > Build Tools > Gradle` se o **Gradle JDK** aponta para a versão 25.
5. Selecione o dispositivo/emulador e clique em **Run** (`Shift + F10`).

---

**Opção B: VS Code**

1. Instale as extensões: **Kotlin**, **Extension Pack for Java** e **Gradle for Java**.
2. Crie um arquivo `local.properties` na raiz do projeto apontando para seu SDK local:

```properties
sdk.dir=/seu/caminho/para/o/android/sdk
```

3. Com um dispositivo ou emulador ativo, rode no terminal:
* **Windows:** `.\gradlew.bat installDebug`
* **macOS/Linux:** `./gradlew installDebug`

---

### Outros Comandos Utilitários (Gradle Wrapper)

* **Limpar a build:**
* **Windows:** `.\gradlew.bat clean` | **macOS/Linux:** `./gradlew clean`


* **Gerar APK de Debug:**
* **Windows:** `.\gradlew.bat assembleDebug` | **macOS/Linux:** `./gradlew assembleDebug`


* **Executar Testes Unitários:**
* **Windows:** `.\gradlew.bat test` | **macOS/Linux:** `./gradlew test`
