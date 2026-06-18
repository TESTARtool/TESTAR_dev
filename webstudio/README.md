# TESTAR Web Studio

`webstudio` is the browser-based studio module for TESTAR.

It is intended to cover:

- workspace editing
- composition and policy configuration
- validation
- local execution control
- future remote execution support

## Architecture

The module currently has three main parts.

### 1. Java backend

Location:

- `webstudio/src/org/testar/webstudio`

Responsibilities:

- start the embedded Javalin server
- expose backend APIs such as:
  - `/api/health`
  - `/api/workspaces`
  - `/api/validation/{workspace}`
  - `/api/execution/backends`
  - `/api/execution/status/{backend}`
- serve the built frontend assets

Important classes:

- `org.testar.webstudio.WebStudioMain`
- `org.testar.webstudio.server.WebStudioServer`
- `org.testar.webstudio.workspace.WorkspaceService`

### 2. Svelte frontend

Location:

- `webstudio/frontend`

Responsibilities:

- render the browser UI
- call the backend APIs
- evolve into the main workspace/execution studio interface

The frontend is built with:

- Svelte
- Vite

### 3. Generated frontend assets

Location after build:

- `webstudio/target/generated/webstudio-frontend/web`

These generated static files are served by the Java backend at runtime.

## Transport model

Current intended usage:

- local development runs over HTTP
- shared or remote deployment should later use HTTPS, preferably through a reverse proxy

Default backend port:

- `8888`

Port override:

- `-PwebstudioPort=9000`

## Gradle commands

### Build frontend only

Build the Svelte frontend into generated static assets:

```powershell
.\gradlew.bat :webstudio:buildWebStudioFrontend
```

### Run backend only

Run the Javalin backend using the already-built frontend assets:

```powershell
.\gradlew.bat :webstudio:runWebStudioServer --console=plain
```

This requires the frontend assets to already exist.

### Build frontend and run everything

Build the frontend and then run the backend:

```powershell
.\gradlew.bat :webstudio:runWebStudio --console=plain
```

This is the normal integrated command.

## CLI mode prerequisites

The Web Studio `Run CLI Mode` page depends on the CLI distribution artifacts.

Before using CLI mode, prepare both the main TESTAR install distribution and the CLI distribution:

```powershell
.\gradlew.bat :testar:installDist
.\gradlew.bat :cli:cliDistribution
```

If you want to use the agent-driven CLI execution, also install the Codex agent runtime:

```powershell
.\gradlew.bat :agent:installCodexAgent
```

The CLI distribution now includes a profile-aware example under:

- `cli_generic`

CLI profiles are separate from the scriptless workspace selector used by the rest of Web Studio.

Web Studio `Run CLI Mode` starts manual sessions against one selected CLI profile, for example:

```text
startSession webdriver https://para.testar.org/ cli_generic
```

If the CLI profile is omitted, the default is:

- `settings/cli_generic/test.settings`

If CLI mode still fails, inspect `cli-debug.log` from the Web Studio debug files view.

For agent-driven CLI execution, also ensure the environment where Web Studio runs has the required API key configured, for example:

- `OPENAI_API_KEY`

### Stop the running backend

Stop processes listening on the configured Web Studio port:

```powershell
.\gradlew.bat :webstudio:stopWebStudioServer
```

With a custom port:

```powershell
.\gradlew.bat :webstudio:stopWebStudioServer -PwebstudioPort=9000
```

### Compile Java only

Compile the backend module:

```powershell
.\gradlew.bat :webstudio:compileJava -x test
```

## Frontend local development

If you want to work directly inside the frontend:

```powershell
cd webstudio\frontend
npm install
npm run dev
```

To build frontend assets manually:

```powershell
cd webstudio\frontend
npm run build
```

## Current direction

Short term:

- expand workspace read/edit APIs
- add Svelte pages for `test.settings`, `composition.properties`, and `policies.properties`
- add frontend dev-server proxying to the Java backend

Long term:

- local scriptless execution control
- CLI session console
- remote execution support
