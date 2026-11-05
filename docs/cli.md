# Controlando o app pelo adb (`tools/NEXTFY-cli`)

Builds debug e beta respondem a alguns comandos enviados pelo adb, então um teste (ou um agente) verifica um recurso
pelos próprios caminhos de código do app sem capturas de tela ou toques. Releases estáveis deixam o receptor
desligado no manifesto, e ele não responde nada fora de builds debug e beta mesmo que ligado.

```
tools/NEXTFY-cli [-p app.NEXTFY.dev] [-s serial] [-t 40] [--raw] <comando> [args…]
```

Cada comando é uma transmissão (`<package>.CLI`, mantida no shell adb pela permissão `DUMP`).
O app escreve sua resposta JSON em `/sdcard/Android/data/<package>/files/cli/<request>.json` e no
logcat sob `NEXTFYCli`; o script lê de volta, imprime o `result` e exclui o arquivo.

Códigos de saída: `0` ok, `1` o comando falhou (o app respondeu um erro, no stderr, ou sem resposta no
tempo), `2` dispositivo inalcançável (`adb get-state` não é `device`, ou vários dispositivos e nenhum nomeado),
`3` o adb falhou, `4` o app não está instalado (`pm path`), `5` desativado neste build (um build estável
não tem receptor CLI: deixa o resultado da transmissão em 0, onde o receptor define 1; `ext.why` é
somente debug). O script sempre almeja um dispositivo: `-s` ou `ANDROID_SERIAL`, senão o único que o adb
lista; com vários e nenhum nomeado ele recusa. `ADB`, `ANDROID_SERIAL` e `NEXTFY_PACKAGE` substituem
os padrões (o beta é `-p app.NEXTFY`).

## Comandos

| Comando | Faz |
| --- | --- |
| `status` | Versão, debug/beta, logado ou não, o que toca (uri, posição, duração), extensões instaladas, se a tela está aberta. `player` diz quem toca: `local` (o próprio player do app; `source` é `audio-source`, `download` ou `file`, e `extension` o id da extensão que deu o stream) ou `engine` (Spotify); `routing` é verdadeiro enquanto as extensões são perguntadas por uma música; enquanto o player local tem uma fila, `playWhenReady` diz se está para tocar e `playbackState` se está `idle` (parado por erro), `buffering`, `ready` ou `ended`. `mirror` é o espelho do Connect (Configurações, "Contar reproduções de extensão no Spotify"): `on` (a configuração), `running`, a música que mantém a página (`uri`, `paused`, `landed` assim que a página tocou, `webPositionMs`), `handedAway` (reprodução foi para outro dispositivo), `failedUri` (uma música que a página não tocaria), e a página por baixo: `pageUri`, `pagePaused`, `pagePositionMs`, `hushed` (não ouvida), `quality` (o nível dado à página: `low` enquanto espelha), `bitrate` e `codec` do que ela streama, `playsElsewhere` (outro dispositivo tem Connect), `activeDevice`. |
| `ext.list` | Extensões instaladas: versão, ativada, rodando, último erro, ações, configurações (secretas mascaradas), e seu último probe `status`: `status` (`live`, `inactive`, `unknown` sem resposta, `not probed`), `inactive` (pulado para áudio e downloads de músicas) e `statusDetail`. |
| `ext.install <zip>` | Instala um pacote com tudo o que pede, sem o prompt. Um zip no computador é enviado primeiro para `…/files/cli/in/`. |
| `ext.uninstall <id>` | Como o botão Desinstalar da página dela. |
| `ext.enable <id>` / `ext.disable <id>` | Liga (conflitos resolvidos para ela) ou desliga. |
| `ext.setting <id> <key> <value>` | Define uma configuração. `true`, `false`, números e JSON permanecem JSON, qualquer outra coisa é uma string, `null` volta ao padrão. A extensão recebe seu evento `setting` após a pausa usual. |
| `ext.press <id> <key>` | Pressiona um botão de configuração (evento `setting` com `pressed: true`). |
| `ext.call <id> <hook> [json]` | Chama um hook dessa extensão sozinha (`lyrics`, `audio-source`, `download-source`, `trackinfo`, `home`, `search`, `menu`, `player`, `page`, `message`, `tap`, `status`) e imprime sua resposta, mascarada; `null` quando seu script não fornece esse hook. Perguntado enquanto o app inicia, espera as extensões carregarem. |
| `ext.log <id> [lines]` | Suas últimas linhas de log (50 por padrão), mascaradas. |
| `ext.storage <id>` | Suas chaves de armazenamento e tamanho, nunca os valores. |
| `ext.why [n]` | As últimas `n` (1 por padrão, 20 no máximo) reproduções através das extensões: fontes perguntadas, resultado e tempo, puladas e por que, escolhida, como seu endereço abriu, anunciado e qualidade detectada, fallback, erro. Endereços sem consulta, mascarados. Somente builds debug. |
| `play <spotify:…> [context]`, `pause`, `resume`, `next`, `previous` | Os próprios comandos do player. |
| `nav <route>` | A tela aberta navega para uma rota (`settings`, `extensions`, `extension/<id>`, `queue`…). |

## O que nunca sai do telefone

Valores de armazenamento; configurações secretas e configurações que o manifesto não lista; em logs, erros e respostas de
hook, qualquer valor armazenado ou secreto, credenciais `Bearer`/`Basic`, cabeçalhos `Cookie`/`Authorization`,
JWTs, valores de campos e parâmetros de URL nomeados como token, chave, assinatura,
sessão, cookie ou senha. Todos substituídos por `***`. Apenas estado de extensão e player é acessível;
nenhum comando lê os próprios tokens de conta do app.

## Ponta a ponta

Uma amostra de extensão de letras, desde empacotar até sua resposta, da raiz do repositório com o build debug no telefone:

```bash
cd base && ./gradlew -q :extensions-cli:installDist && cd ..
base/extensions-cli/build/install/NEXTFY-ext/bin/NEXTFY-ext pack docs/extensions/samples/lrclib-lyrics /tmp/NEXTFY-cli
tools/NEXTFY-cli ext.install /tmp/NEXTFY-cli/app.NEXTFY.samples.lrclib-*.zip
tools/NEXTFY-cli ext.list
tools/NEXTFY-cli ext.call app.NEXTFY.samples.lrclib lyrics '{"title":"Get Lucky","artist":"Daft Punk","durationMs":369000}'
tools/NEXTFY-cli ext.log app.NEXTFY.samples.lrclib 20
tools/NEXTFY-cli play spotify:track:69kOkLUCkxIZYexIgSG8rq
tools/NEXTFY-cli status
tools/NEXTFY-cli ext.disable app.NEXTFY.samples.lrclib
```

Em um script, `--raw` imprime a resposta inteira (`{"ok":…,"cmd":…,"result":…}`) e o código de saída diz se
funcionou, e.g. `tools/NEXTFY-cli --raw ext.list | jq '.result[].id'`.
