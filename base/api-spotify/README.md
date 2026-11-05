# api-spotify

Implementação do Spotify da API do NEXTFY (`:api`), sobre o Web Player oficial.

## Módulos

| Módulo | Papel |
|---|---|
| `:api` | Contrato puro Kotlin que a UI vê: modelos e interfaces, sem Android, sem detalhes do Spotify |
| `:api-spotify` | Este módulo: `SpotifyApi` implementa `:api` |
| `:host-cdp` | Host desktop (Chrome sobre CDP) e os testes de integração ao vivo |

Leituras vão direto ao Spotify sobre HTTP (`SpotifyHttp`: o web token da página mais um token de cliente
cunhado aqui). Reprodução, gravações de biblioteca, pastas, Connect e Jam vão através dos próprios serviços
da página via ponte. `SpotifyApi.raw` alcança todas as 179 operações GraphQL
(`gql-ops.json`, variáveis em `gql-schema.json`) e os 89 serviços de página para qualquer coisa não tipada.

## Cobertura

Tudo isso é exercitado pelos testes ao vivo de `host-cdp` contra uma conta gratuita:

| Domínio | Métodos |
|---|---|
| Player | play (qualquer contexto), pause, resume, seek, next, previous, repeat, shuffle, queue add/remove/clear, volume, speed (apenas podcasts) |
| Search | todos, tracks, artists, albums, playlists, shows, episodes, audiobooks, users, genres, suggestions, recent, clear recent |
| Catalog | track, album, album tracks, artist, top tracks, discography, related artists, appears on, similar albums |
| Library | contains/add/remove, library list com filtros, sort e pastas, liked songs, pin/unpin, create/rename/delete folders, move to folder |
| Playlists | playlist, tracks (paginado, com row uids), sorted tracks (lado do cliente, ascendente ou descendente), mine, editable, create, rename, description, public, collaborative, delete, follow/unfollow, add/remove/move tracks, recommended songs |
| Home | feed com filtros, categorias, páginas de categoria |
| Podcasts | show, episodes, episode, audiobook, chapters, saved episodes, save or unsave an episode |
| Users | me, public profile, top artists/tracks, follow/unfollow/is following, history |
| Track extras | lyrics (line and word sync, colours), canvas, credits, radio for a track or artist, taste-profile exclusion, don't play this artist |
| Devices | list, transfer, pull here |
| Jam | availability, session, start, join, leave, end, invite link, remove member, queue-only, participant volume |

Adicionados sem verificação ao vivo (mesmo mecanismo de um vizinho verificado, veja as notas em
[feature-inventory.md](../../docs/api/feature-inventory.md)): don't play this artist (`artistban`),
save or unsave an episode (`listenlater`), public and collaborative (`PlaylistAPI.updateDetails`,
read back with `getMetadata`). A lista completa de recursos, com o que precisa de uma captura, está naquele arquivo.

Limites vistos no Web Player com uma conta gratuita:
- Jam: Spotify responde `RESTRICTED`, então `start` falha; join e o resto estão conectados mas não verificados.
- Sessão privada: não suportada pelo Web Player (`isSupported: false`).
- Atividade de amigos: não implementada pelo Web Player; `friendActivity()` retorna uma lista vazia.
- Transferência de dispositivo: precisa de um segundo dispositivo Connect para verificar.

Ferramentas (`tools/`): `refresh_gql_ops.py` (operation hashes dos bundles públicos),
`extract_gql_variables.py` (variáveis de capturas), `infer_gql_variables.py`,
`build_gql_schema.py` (variáveis e tipos necessários do servidor), `capability_report.py`.

## Player bridge

[`src/main/resources/bridge/player-bridge.js`](src/main/resources/bridge/player-bridge.js) roda dentro da
página do Web Player oficial (open.spotify.com), que continua fazendo a reprodução com a
Widevine CDM do navegador. A ponte encontra o registro de serviços da página através da
árvore de contexto do React e chama seu `PlayerAPI`, `PlaybackAPI`, `LibraryAPI` e `TokenProvider`.
Ela não remenda nada na página.

O mesmo arquivo roda no WebView do Android e no Chrome desktop sobre CDP.

### Canais

| Direção | Android | Desktop (CDP) |
|---|---|---|
| Host → página | `evaluateJavascript("NEXTFY.call(id, method, args)")` | `Runtime.evaluate`, mesma chamada |
| Página → host | `NEXTFYHost.postMessage(json)` (web message listener, open.spotify.com apenas) | `Runtime.addBinding("__mspHostPost")` |

`NEXTFY.call` também retorna uma Promise, mas os hosts devem confiar na mensagem `result`:
o `evaluateJavascript` do Android não espera por Promises.

### Mensagens (página → host)

| `type` | Campos |
|---|---|
| `ready` | `version` |
| `state` | `state`: veja abaixo, ou `null` quando não existe sessão de reprodução |
| `queue` | `queue`: `{current, queued[], nextUp[]}` de tracks |
| `result` | `id`, `ok`, `value` ou `error` |
| `error` | `error` (falha de boot) |

`state`: `{track, contextUri, isPaused, isBuffering, positionMs, timestamp, durationMs,
shuffle, repeat (0 desligado, 1 contexto, 2 faixa), speed, restrictions, playbackId}`.
`positionMs` é a posição em `timestamp` (epoch ms). Extrapole-o enquanto toca.

`track`: `{kind ("track", "episode" ou "ad"), uri, uid, name, advertiser, durationMs, artists[{uri, name}],
album{uri, name}, image,
explicit, mediaType, provider}`. Um ad tem `kind: "ad"`, uma `spotify:ad:` uri, um `advertiser`,
e restrições que proíbem skip e seek.

### Métodos

| Método | Argumentos | Retorna |
|---|---|---|
| `bridge.version` | | `1` |
| `player.state` | | state ou `null` |
| `player.queue` | | queue |
| `player.play` | `contextUri`, opcional `trackUri`, `trackUid`, `index`, `positionMs` | |
| `player.pause` / `player.resume` | | |
| `player.seek` | `positionMs` | |
| `player.next` / `player.previous` | | |
| `player.setShuffle` | `on` | |
| `player.setRepeat` | `mode` (0, 1, 2) | |
| `queue.add` | `uris[]` | |
| `queue.remove` | `items[{uri, uid}]` | |
| `queue.clear` | | |
| `volume.get` / `volume.set` | `level` (0 a 1) | número |
| `library.contains` | `uris[]` | `boolean[]` |
| `library.add` / `library.remove` | `uris[]` (tracks, albums, artists) | |
| `token.get` | | `{accessToken, expiresAtMs}` para a HTTP API |

Notas:
- `player.previous` perto do início de uma faixa volta. Depois, reinicia a faixa.
- `queue.add` cai em cerca de um segundo, e uma mensagem `queue` segue.
- Um comando pode ser recusado por `restrictions`, por exemplo enquanto a próxima faixa carrega. A mensagem de erro diz isso.

### Testes

Testes Kotlin ao vivo: `./gradlew :host-cdp:test -Pcdp=http://localhost:9222`.

O antigo harness Python [`bridge-tests/run_bridge_tests.py`](bridge-tests/run_bridge_tests.py) controla um Web Player logado em
Google Chrome desktop (Widevine incluído) sobre CDP. Cada teste restaura o que muda.

```bash
google-chrome --user-data-dir="$HOME/.NEXTFY-chrome" --remote-debugging-port=9222
```

```bash
python run_bridge_tests.py            # todos os testes
python run_bridge_tests.py -k queue   # alguns testes (separados por vírgula)
```

Não coberto ainda: anúncios em contas gratuitas, Connect (outros dispositivos), podcasts.
