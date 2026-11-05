# Sessão e transporte do Web Player

Notas de sala limpa de capturas de rede do open.spotify.com (conta gratuita, Chrome no
Linux, 2026-10-15, builds `1.3.6.1858` e `1.3.6.1874`). Os valores abaixo são placeholders:
`{token}` web access token, `{ct}` client token, `{dev}` 40-hex device id, `{conn}`
dealer connection id, `{user}` account id. Veja CAPTURE-MAP.md para as chamadas de feature.

## 1. Tokens

### Web access token

`GET https://open.spotify.com/api/token?reason={r}&productType=web-player&totp={code}&totpServer={code}&totpVer={n}`

| Param | Significado |
|---|---|
| `reason` | `init` no carregamento da página, `transport` quando o player atualiza antes de expirar |
| `productType` | sempre `web-player` |
| `totp`, `totpServer` | códigos TOTP de 6 dígitos, calculados na página a partir do horário local e do horário do servidor (geralmente iguais) |
| `totpVer` | versão do segredo TOTP (61 na captura) |

Auth é o cookie `sp_dc` (cookie de sessão definido por accounts.spotify.com; `sp_t`, `sp_key`
também enviados). Sem cabeçalho Authorization. Antes de uma atualização `transport` a página chama
`GET open.spotify.com/api/server-time` -> `{"serverTime": <unix seconds>}`.

Resposta (JSON): `clientId`, `accessToken`, `accessTokenExpirationTimestampMs`,
`isAnonymous`, `_notes`. Duração é cerca de 1 hora (55 min restantes na primeira busca). O
servidor armazena o token em cache por cookie: recarregamentos dentro da hora retornam o mesmo token e
expiração. Uma chamada `transport` retornou um token novo com uma hora nova.

O token é usado como `authorization: Bearer {token}` em pathfinder e spclient, e como
o parâmetro de consulta `access_token` do dealer WebSocket.

### Client token

`POST https://clienttoken.spotify.com/v1/clienttoken`, `content-type` e `accept`
`application/json`, sem cabeçalho auth.

```json
{"client_data": {
  "client_version": "1.3.6.1858.gc66299947cb4",
  "client_id": "<mesmo valor que clientId de /api/token>",
  "js_sdk_data": {"device_brand": "unknown", "device_model": "unknown", "os": "linux",
                  "os_version": "unknown", "device_id": "<36-char id, não {dev}>",
                  "device_type": "computer"}}}
```

Resposta: `response_type` = `RESPONSE_GRANTED_TOKEN_RESPONSE`, `granted_token`:
`token`, `expires_after_seconds` (1216800, cerca de 14 dias), `refresh_after_seconds`
(1209600, 14 dias), `domains` (`spotify.com`, `spotify.net`). Buscado uma vez por
carregamento de página; pode ser cacheado por dias.

### Cabeçalhos em chamadas de API

| Cabeçalho | api-partner (pathfinder) | spclient.wg | {region}-spclient | Significado |
|---|---|---|---|---|
| `authorization` | sempre | quase sempre | na maioria | `Bearer {token}` |
| `client-token` | sempre | sempre | na maioria | `{ct}` de clienttoken |
| `app-platform` | sim | sim | algumas | `WebPlayer` |
| `spotify-app-version` | sim | sim | algumas | build `clientVersion`, ex. `1.3.6.1858.gc66299947cb4` |
| `accept-language` | `fr` (UI locale) | `fr` | lista do navegador | idioma do conteúdo |
| `accept` | `application/json` | `application/json` ou `application/protobuf` | `*/*` | |
| `content-type` | `application/json;charset=UTF-8` | JSON | JSON ou `text/plain` | |

Cabeçalhos extras por endpoint: `x-spotify-connection-id: {conn}` em
`PUT connect-state/v1/devices/hobs_*`; `x-cache-hint: <random 16-hex>` em
`GET connect-state/v1/cluster`; `x-client-id` em `pendragon/.../FetchMessageList`.

Exceções: `gabo-receiver-service/public/v3/events` e
`widevine-license/v1/application-certificate` não carregam `authorization`;
`gabo-receiver-service/*/events` não carregam `client-token`. Toda chamada pathfinder
enviou tanto `authorization` quanto `client-token`, então um cliente deve sempre enviar ambos;
se apenas o web token é aceito não foi testado (captura offline).

Chamadas pathfinder de `getAlbum` e `queryArtistOverview` às vezes enviaram
`spotify-app-version: 896000000` em vez da versão pontilhada; ambas foram aceitas.

## 2. Hosts

`GET https://apresolve.spotify.com/?type=dealer-g2&type=spclient` (sem auth). Resposta:

```json
{"dealer-g2": ["gew4-dealer.g2.spotify.com:443", "guc3-dealer.g2.spotify.com:443", "..."],
 "spclient":  ["gew4-spclient.spotify.com:443",   "guc3-spclient.spotify.com:443",   "..."]}
```

Listas estão ordenadas por preferência (mais próximo primeiro); a página pegou a primeira entrada de
cada (`gew4-*` da Europa) e faz fallback para a próxima em caso de falha. Chamado várias
vezes por carregamento de página.

| Host | Usado para |
|---|---|
| `api-partner.spotify.com` | GraphQL: `POST /pathfinder/v2/query` (mais uma chamada `v1`, veja 3) |
| `spclient.wg.spotify.com` | Alias global fixo. APIs de conteúdo e biblioteca: `metadata/4`, `extended-metadata`, `collection/v2`, `playlist/v2`, `playlistextender`, `color-lyrics`, `timesynced-text-distribution`, `ads/v2/config`, `gander`, `library-import`, `widevine-license/.../application-certificate` |
| `{region}-spclient.spotify.com` (de apresolve) | APIs de sessão e reprodução: `track-playback/v1`, `connect-state/v1`, `manifests`, `storage-resolve`, `widevine-license/.../license`, `melody`, `gabo-receiver-service`, `remote-config-resolver`, `pendragon`, `social-connect` |

Ambos os hosts aceitam os mesmos cabeçalhos de auth. Connect-state e track-playback devem usar o
host regional (mesma região que o dealer, já que o connection id do dealer está vinculado a ele).

## 3. GraphQL persisted queries

Corpo de `POST https://api-partner.spotify.com/pathfinder/v2/query`:

```json
{"variables": {...}, "operationName": "fetchPlaylist",
 "extensions": {"persistedQuery": {"version": 1, "sha256Hash": "<64 hex>"}}}
```

Uma chamada estranha vai para `pathfinder/v1/query` sem `operationName`
(`variables.uris` = lista de URIs de faixas, resposta `data.lookupEntities`, hash
`f952da037440f694cc6925b9e3f649d39077a744c4db7dfba01cb883723f4f77`); é hard-coded
em `vendor~web-player.*.js` como uma string raw `sha256Hash:"..."`.

### Onde hashes vivem nos bundles

Cada operação é uma constante de módulo construída com um construtor de classe minificado:

```js
let p=new C.l("concertCount","query","29be9d48...b141",null)
```

Regex de extração (funcionou em todos os bundles, sem falsos positivos; o alias da classe varia):

```
new [\w$]+(?:\.[\w$]+)?\("(\w+)","(query|mutation|subscription)","([0-9a-f]{64})"
```

Grupos: operationName, kind, sha256Hash. Uma segunda passagem para literais raw:
`sha256Hash:"([0-9a-f]{64})"`.

A maioria das operações das páginas principais (`home`, `fetchPlaylist`, `libraryV3`, `getAlbum`,
`queryArtistOverview`, `addToLibrary`...) está definida no bundle de entrada de 4.4 MB
`web-player.{hash}.js`, cujo corpo não foi capturado. Seus hashes na tabela vêm dos
corpos de requisição. Cada operação teve um único hash em ambos os builds vistos.

### Atualizando hashes automaticamente

1. `GET https://open.spotify.com/` (HTML, com o cookie de sessão ou anônimo).
2. Leia `<script id="appServerConfig" type="text/plain">` (base64 JSON): campos
   `clientVersion` (use para `spotify-app-version` e `client_version` do clienttoken),
   `buildVersion` (`web-player_{date}_{ms}_{sha}`), `buildDate`, `serverTime`,
   `market`, `locale`, `isPremium`, `isAnonymous`. Compare `buildVersion` com o
   cache; atualize apenas quando mudar.
3. Leia `<script id="__CDN_FILE_URLS__" type="text/plain">` (base64 JSON map, cerca de
   1000 entradas): chave `build/web-player/{chunk}.js` (e o nome com hash) ->
   URL completa `https://open.spotifycdn.com/cdn/build/web-player/{chunk}.{hash8}.js`.
   Cerca de 164 chunks JS do web-player, além de `web-player.js` (entrada),
   `vendor~web-player.js`, `encore~web-player.js`. Os scripts de entrada, vendor e encore
   também são tags `<script src>` simples no HTML.
4. Baixe o bundle de entrada e todos os chunks `build/web-player/*.js`, rode o regex,
   mescle em um mapa `operationName -> hash`, persista com a versão do build.
5. Fallback em tempo de execução: se o pathfinder retornar um erro tipo `PersistedQueryNotFound`
   para um op, execute o passo 1-4 uma vez e tente novamente (formato do erro não capturado).

### Tabela

99 operações: 69 definidas nos corpos de chunks capturados, 30 a mais vistas apenas em corpos
de requisição (estão no bundle de entrada não capturado). `kind` é `-` quando apenas a requisição
foi vista.

| operationName | kind | sha256Hash | visto em |
|---|---|---|---|
| `accountAttributes` | - | `d48062d0df0a256ff78ed9e232b83d238dcd710b371efd304b0e178b62595582` | requisição (bundle principal) |
| `addToLibrary` | - | `896ebcb47815681340860d121cb5d494e157e2a78d3950385cd54e0393c67148` | requisição (bundle principal) |
| `addToPlaylist` | - | `b907ad6b088d5bf7680fa81198996d58398c2d55dfcdf59fcc959e0be98a418a` | requisição (bundle principal) |
| `applyCurations` | - | `05b739a3a73091c213385233b9d3ed8a857c2ca29d2eebadb3d04ed12e288697` | requisição (bundle principal) |
| `areEntitiesInLibrary` | - | `134337999233cc6fdd6b1e6dbf94841409f04a946c5c7b744b09ba0dfe5a85ed` | requisição (bundle principal) |
| `ArtistConcerts` | query | `ef53c43b865496b9890b7167eab1dc614a8949ef9451b3c41184ea888de8bd2b` | bundle `xpui-routes-artist.8dd6925c` |
| `ArtistConcertsPageLocation` | query | `320698465a352f0d0247ec8ed02471244106d4199820f99de4d0a785561c2b03` | bundle `xpui-routes-artist.8dd6925c` |
| `browseAll` | query | `dbd8b55e09a58afc52eab438bc228ba28fd72ac2f2148c6c26354980e4579001` | bundle `xpui-routes-search.1472683a` + requisição |
| `canvas` | - | `575138ab27cd5c1b3e54da54d0a7cc8d85485402de26340c2145f0f6bb5e7a9f` | requisição (bundle principal) |
| `centralisedStatePlayerOptions` | - | `e2dcfcab470854d4d1c7cb1a851438f14fe0a94d57db7f0b9dde492559d5395d` | requisição (bundle principal) |
| `concert` | query | `9b582a69061f6648858515c2aa6d4a2273ac49b74eb5dbc612bc1299a9f2f13e` | bundle `xpui-root-dialogs.7582d917` |
| `concertCount` | query | `29be9d486e073a49268e13ed9e2d2180187e669fcb7a19b98011aca7ab61b141` | bundle `1272.990d4950` |
| `concertLocationsByLatLon` | query | `8a059d072a17a1199feb21fe846271f1680eda87010c832852ced0c55c6c7c96` | bundle `1272.990d4950` |
| `decorateContextTracks` | - | `383de00240775c39a6afe0b1055dc562b2a3930894201f9762f3fc32a74971c7` | requisição (bundle principal) |
| `decorateQueuedByUsers` | query | `33fe7085b4a9dde404955eeee4f8f93aa7a8e55028058354bc707727d8a7890d` | bundle `dwp-panel-section.0a1acab1` + requisição |
| `deleteLocation` | mutation | `2f681a7c30da3bc905c469374aa2983d0d00a1b6ba88e5fdd6a23caa02057971` | bundle `1272.990d4950` |
| `editablePlaylists` | - | `d5c4b8096437dcc2ac9528c91dfcd299e35b747cda2f8f75d28f41f49c5092ba` | requisição (bundle principal) |
| `feedBaselineLookup` | - | `318190494c436b51ab995367bffc66dcdb49dfa023792c60ac15dcb6dfbec42f` | requisição (bundle principal) |
| `fetchEntitiesForRecentlyPlayed` | - | `cf5d2e94ffd82788470788ae1f6090cc3e9e774fb8fd383580634c6e6f50f7be` | requisição (bundle principal) |
| `fetchExtractedColorAndImageForAlbumEntity` | query | `b2d6d99fb6237952dfa6638385f9c6085f1b1fb9d0468753ca6ad98ff45adc6f` | bundle `xpui-root-dialogs.7582d917` |
| `fetchExtractedColorAndImageForArtistEntity` | query | `b2d6d99fb6237952dfa6638385f9c6085f1b1fb9d0468753ca6ad98ff45adc6f` | bundle `xpui-root-dialogs.7582d917` |
| `fetchExtractedColorAndImageForEpisodeEntity` | query | `b2d6d99fb6237952dfa6638385f9c6085f1b1fb9d0468753ca6ad98ff45adc6f` | bundle `xpui-root-dialogs.7582d917` |
| `fetchExtractedColorAndImageForPlaylistEntity` | query | `b2d6d99fb6237952dfa6638385f9c6085f1b1fb9d0468753ca6ad98ff45adc6f` | bundle `xpui-root-dialogs.7582d917` |
| `fetchExtractedColorAndImageForPodcastEntity` | query | `b2d6d99fb6237952dfa6638385f9c6085f1b1fb9d0468753ca6ad98ff45adc6f` | bundle `xpui-root-dialogs.7582d917` |
| `fetchExtractedColorAndImageForTrackEntity` | query | `b2d6d99fb6237952dfa6638385f9c6085f1b1fb9d0468753ca6ad98ff45adc6f` | bundle `xpui-root-dialogs.7582d917` |
| `fetchExtractedColorForAlbumEntity` | query | `b2d6d99fb6237952dfa6638385f9c6085f1b1fb9d0468753ca6ad98ff45adc6f` | bundle `xpui-root-dialogs.7582d917` |
| `fetchExtractedColorForArtistEntity` | query | `b2d6d99fb6237952dfa6638385f9c6085f1b1fb9d0468753ca6ad98ff45adc6f` | bundle `xpui-root-dialogs.7582d917` |
| `fetchExtractedColorForEpisodeEntity` | query | `b2d6d99fb6237952dfa6638385f9c6085f1b1fb9d0468753ca6ad98ff45adc6f` | bundle `xpui-root-dialogs.7582d917` |
| `fetchExtractedColorForPlaylistEntity` | query | `b2d6d99fb6237952dfa6638385f9c6085f1b1fb9d0468753ca6ad98ff45adc6f` | bundle `xpui-root-dialogs.7582d917` |
| `fetchExtractedColorForPodcastEntity` | query | `b2d6d99fb6237952dfa6638385f9c6085f1b1fb9d0468753ca6ad98ff45adc6f` | bundle `xpui-root-dialogs.7582d917` |
| `fetchExtractedColorForTrackEntity` | query | `b2d6d99fb6237952dfa6638385f9c6085f1b1fb9d0468753ca6ad98ff45adc6f` | bundle `xpui-root-dialogs.7582d917` |
| `fetchExtractedColors` | - | `36e90fcaea00d47c695fce31874efeb2519b97d4cd0ee1abfb4f8dc9348596ea` | requisição (bundle principal) |
| `fetchPlaylist` | - | `21ea11a6ac75a5f3e01fd365d29c18e0a4a8b7dc09f89bbb5d73910190c6acaf` | requisição (bundle principal) |
| `fetchPlaylistContents` | - | `21ea11a6ac75a5f3e01fd365d29c18e0a4a8b7dc09f89bbb5d73910190c6acaf` | requisição (bundle principal) |
| `fetchPlaylistMetadata` | - | `21ea11a6ac75a5f3e01fd365d29c18e0a4a8b7dc09f89bbb5d73910190c6acaf` | requisição (bundle principal) |
| `getAlbum` | - | `11c9cad9cb24c4f9e25ef9456f68621cfd6006a90f679e729c1d2f2fd70dfe16` | requisição (bundle principal) |
| `getAudiobooksMetadata` | query | `523c71c64749a628f83e6b31a122c76663243730bf02df01fd64abf0f62f572f` | bundle `1675.fa3bc7da` |
| `getDynamicColorsByUris` | - | `f0f112945d6d745bd8ff790317bbf8d310036da75df33130490e9d6dc96c59d9` | requisição (bundle principal) |
| `getTrack` | - | `a8ef9e9f02b836feb0da3003c31dbb30decc6f4b473ef89ca88c882386d668de` | requisição (bundle principal) |
| `home` | - | `5fb7da7a03ee2776c0670856609cb85a189a3cecc36cc160b91cde79063b9cf1` | requisição (bundle principal) |
| `inferredUserLocation` | query | `5db4c507ea735d2a1f37bd1166eca2c1a0e3387bb875ebca5d6031b6eccceeba` | bundle `1272.990d4950` |
| `internalLinkRecommenderTrack` | query | `c77098ee9d6ee8ad3eb844938722db60570d040b49f41f5ec6e7be9160a7c86b` | bundle `xpui-routes-track-v2.f48fb16e` + requisição |
| `isCurated` | - | `e4ed1f91a2cc5415befedb85acf8671dc1a4bf3ca1a5b945a6386101a22e28a6` | requisição (bundle principal) |
| `libraryV3` | - | `390c78e5b951029bad359785e69b07b536a509c581cbcd0aded5e5067f187455` | requisição (bundle principal) |
| `lookupChildEntities` | query | `91ce02e32b19123de231dc8de91fe4b9ab84eca087d4c015549308d77fbb6d10` | bundle `7338.5565a19f` + requisição |
| `npvV2TrackSupplementalSections` | query | `3268e307f08330b620de5c161a03b48acf66ec57164eac446e0653694a485764` | bundle `dwp-panel-section.0a1acab1` + requisição |
| `playlistPermissions` | - | `e43d1d35f231cf289c23c9d9c489f4a4f502e4eda09839c530608f107b6556b8` | requisição (bundle principal) |
| `playlistSection` | query | `2615df403a9043c1d7d3094fbeb4c9653b07b11a33d8081fbd31f0f7959ff4a1` | bundle `xpui-routes-playlist.7090f328` |
| `profileAttributes` | - | `08ffb4730af3746e04a8301396f20875dbbce10c75243803091a9274eacc8ac0` | requisição (bundle principal) |
| `queryAlbumMerch` | query | `3ef44ed6f17be67299538fe77faffab4075aeaf9e1085f10fc835592266711b5` | bundle `xpui-routes-album.7c466d0f` + requisição |
| `queryArtistAboutModal` | query | `702e29a866e0b4b4fabdf974454512a2c32d93643b1969b7a3a0dd92084f05f6` | bundle `xpui-root-dialogs.7582d917` |
| `queryArtistAppearsOn` | query | `9a4bb7a20d6720fe52d7b47bc001cfa91940ddf5e7113761460b4a288d18a4c1` | bundle `xpui-routes-artist.8dd6925c` |
| `queryArtistDiscographyAlbums` | query | `5e07d323febb57b4a56a42abbf781490e58764aa45feb6e3dc0591564fc56599` | bundle `xpui-routes-artist.8dd6925c` |
| `queryArtistDiscographyAll` | query | `5e07d323febb57b4a56a42abbf781490e58764aa45feb6e3dc0591564fc56599` | bundle `xpui-routes-artist.8dd6925c` |
| `queryArtistDiscographyCompilations` | query | `5e07d323febb57b4a56a42abbf781490e58764aa45feb6e3dc0591564fc56599` | bundle `xpui-routes-artist.8dd6925c` |
| `queryArtistDiscographyOverview` | query | `5e07d323febb57b4a56a42abbf781490e58764aa45feb6e3dc0591564fc56599` | bundle `xpui-routes-artist.8dd6925c` |
| `queryArtistDiscographySingles` | query | `5e07d323febb57b4a56a42abbf781490e58764aa45feb6e3dc0591564fc56599` | bundle `xpui-routes-artist.8dd6925c` |
| `queryArtistDiscoveredOn` | query | `71c2392e4cecf6b48b9ad1311ae08838cbdabcfd189c6bf0c66c2430b8dcfdb1` | bundle `xpui-routes-artist.8dd6925c` |
| `queryArtistFeaturing` | query | `20842d6d9d2d28ef945984b68cb927bb33edd00eab84a8da1667def21f1f2c54` | bundle `xpui-routes-artist.8dd6925c` |
| `queryArtistMinimal` | query | `53d3f76582c49ad0a05dc685955f20dc2a5f2209b192e5446e5e4e623ce23a48` | bundle `xpui-routes-artist.8dd6925c` |
| `queryArtistOverview` | - | `9f8134ef565e78621f1e1793555bd6633c5ac144ae0f89604ed3ae3f80b3c8e6` | requisição (bundle principal) |
| `queryArtistPlaylists` | query | `54f7e5a5a2af05b7dc98526df376a46c6b15c05440c8dfdc8f6cecb1a807eca7` | bundle `xpui-routes-artist.8dd6925c` |
| `queryArtistRelated` | query | `3d031d6cb22a2aa7c8d203d49b49df731f58b1e2799cc38d9876d58771aa66f3` | bundle `xpui-routes-artist.8dd6925c` |
| `queryArtistRelatedVideos` | query | `e4bbb4f420ad5786caa7028e2943542f13c8138f5c40736292b8499ac9672ce6` | bundle `xpui-routes-artist.8dd6925c` |
| `queryInlineCurationSearchAlbum` | query | `122fd4817f7e9cd7b944c3103d9c81a950668423ba9d2008413b5291f9d7e547` | bundle `xpui-routes-playlist.7090f328` |
| `queryInlineCurationSearchArtist` | query | `be42f25d92bdadac4082e6581d35b771c3b5e8bbdd1b76c279d0924a5975f12e` | bundle `xpui-routes-playlist.7090f328` |
| `queryInlineCurationSearchV2` | query | `331ddd828a4db217046345833863f59f907d269947dd6f924009f6ab35743b70` | bundle `xpui-routes-playlist.7090f328` |
| `queryInlineCurationSearchV2Booklists` | query | `331ddd828a4db217046345833863f59f907d269947dd6f924009f6ab35743b70` | bundle `xpui-routes-playlist.7090f328` |
| `queryNpvArtist` | - | `e1ae46a21911a3075c1aa29bf09a6c60f9a45b6a2b1132429f10ad06c299b5d7` | requisição (bundle principal) |
| `queryTrackArtists` | - | `ee2b038198f5e62c679c3996584d9249bbee55fe69fc212271c56492a022c798` | requisição (bundle principal) |
| `queryTrackCreditsGroupedModal` | query | `f135fb9be58a72d041ab5d214d817021a272405d883860468e2627afb01a3ca9` | bundle `xpui-root-dialogs.7582d917` |
| `recentSearches` | - | `ece96d7b82c820fb9938dd1df50dee3e53737d808c3883e3d01c467c423601b0` | requisição (bundle principal) |
| `removeFromLibrary` | - | `896ebcb47815681340860d121cb5d494e157e2a78d3950385cd54e0393c67148` | requisição (bundle principal) |
| `saveLocation` | mutation | `5502351e9f201ae29014ca55d3b24b755ba261a1a9eb35fb498cb4c7df419353` | bundle `1272.990d4950` |
| `searchAlbums` | query | `202cb3305e31e5a0767ba7925f28bd728cf8f8b0217e6da43909056071cd70e9` | bundle `xpui-routes-search.1472683a` + requisição |
| `searchArtists` | query | `7bf95d754fdbe32c8b161fbbe54d1ae50974900df4dce4c8f1afcbcad153224d` | bundle `xpui-routes-search.1472683a` + requisição |
| `searchAudiobooks` | query | `e05ac765d02c084f8783d3c1572b23d57761c43f47eb8b87ce2f9ccced3fa068` | bundle `xpui-routes-search.1472683a` |
| `searchAuthors` | query | `4a9d403a7cbc7e19da5520d619a865472b35382b043bfa458154e73a5c6f46bd` | bundle `xpui-routes-search.1472683a` |
| `searchConcertLocations` | query | `43ededefcba8b3f519fd0c2d6c025dfeec9f742cf47d04a3c3711d95b27deda3` | bundle `1272.990d4950` |
| `searchDesktop` | query | `eef7cc54888d91bdd6802623477873caa3948ae173a0c34fd86827b267e94c03` | bundle `xpui-routes-search.1472683a` |
| `searchEpisodes` | query | `dbc56e15e7c1254f11d499e8647343bbf28a5594e14a8f5c8b1e7eee67a55740` | bundle `xpui-routes-search.1472683a` |
| `searchFullEpisodes` | query | `397a4e93a21d140d088cfd26641133c5146a2f5f08e420654868153e720f5571` | bundle `xpui-routes-search.1472683a` |
| `searchGenres` | query | `9e1c0e056c46239dd1956ea915b988913c87c04ce3dadccdb537774490266f46` | bundle `xpui-routes-search.1472683a` |
| `searchModalEntityPage` | query | `25d73ce01f3011ff8b37f44c23ebbad18610db1a2cec2c84f20c1e822c748524` | bundle `xpui-root-dialogs.7582d917` |
| `searchModalResults` | query | `9a79751378a306d87d9789fa0c4a8f7c7213891c47a3993a0d0b58ff26514e59` | bundle `xpui-root-dialogs.7582d917` |
| `searchPlaylists` | query | `d520014e748f9ea44f7707d8df1819867ac1205e8b7f3e28f22fe5fc858921b1` | bundle `xpui-routes-search.1472683a` + requisição |
| `searchPodcasts` | query | `0195d9f61b43606d490bca64c3456e3593528cea6cc05c7e822c7c42beed0f4e` | bundle `xpui-routes-search.1472683a` |
| `searchSuggestions` | - | `f244254b94c0e824d458ac216e0f8406a73f2f69a9ff52831a2f78fa774ff6ce` | requisição (bundle principal) |
| `searchTopResultsList` | query | `dd78eaff943eba629ed70ee25517b9cea0dcaa41193e2592ae2727660b21892c` | bundle `xpui-routes-search.1472683a` + requisição |
| `searchTopResultsOnly` | query | `cb32d0ff697131db6d34f174f7d2fa931c6a8b6be22aabc8c6cc086da4a0f744` | bundle `xpui-routes-search.1472683a` |
| `searchTracks` | query | `b02683192a98dde7966b5e6655a79eeb62713eab703eda9902c932818dd52751` | bundle `xpui-routes-search.1472683a` + requisição |
| `searchUsers` | query | `8f358dd82e62f61dd4ceaa9f8cd0889e644c9b707f1b724fbfb356a757cb7e5a` | bundle `xpui-routes-search.1472683a` |
| `showItemsPlayedState` | query | `4a070b9bfab2e8537a5271e6839bc3ef51501dcac8170b5fb69a98f967c5fb60` | bundle `xpui-root-dialogs.7582d917` |
| `similarAlbumsBasedOnThisTrack` | - | `1d1f93a737498adca2c892c73af87fc0b052afe4e1a33c989540c32413dfae17` | requisição (bundle principal) |
| `thisisPlaylistExtension` | query | `906c2e14927400c8a71645c647576ce032f837a112d5e6f9375b19c80445c8cf` | bundle `xpui-routes-playlist.7090f328` |
| `trackPreview` | query | `fc26ffc7a1a4f93bd4c2d705649f7dba1de34005b3dc2915549847a9959405d8` | bundle `1675.fa3bc7da` |
| `userLocation` | query | `079939378ca79b67c6d047be9152ea940d21f10bbfa2f5d4cf4d8320d87774c2` | bundle `1272.990d4950` |
| `watchFeedEntity` | query | `d1a805f815ee0c20281330317d8c00c8fce9026686a86c38bc700ccaefb9ac5c` | bundle `dwp-watch-feed-view-container.7da8db88` |
| `watchFeedView` | query | `ce10864fccc53ca77c422143ee027bacf220f9d714e27ec592905334079ad743` | bundle `dwp-watch-feed-view-container.7da8db88` |

## 4. Dealer WebSocket

Conectar: `wss://{dealer-host}/?access_token={token}` (host do apresolve
`dealer-g2`, ex. `gew4-dealer.g2.spotify.com`; único parâmetro de consulta é `access_token`).
Reconectar (novo socket, novo `{conn}`, re-registrar) com um token novo quando expirar; a
captura mostra um novo socket por carregamento de página, e o comportamento exato de atualização não foi isolado.

Frames são texto JSON.

- Cliente -> servidor: apenas `{"type":"ping"}` a cada 30 s.
- Servidor -> cliente: `{"type":"pong"}` por ping.
- Primeiro frame do servidor após abrir: o id da conexão:

```json
{"type": "message", "method": "PUT",
 "uri": "hm://pusher/v1/connections/{conn, url-encoded base64}",
 "headers": {"Spotify-Connection-Id": "{conn}"}}
```

  `{conn}` vai no cabeçalho `x-spotify-connection-id` de
  `PUT connect-state/v1/devices/hobs_*` e no `connection_id` do
  corpo de registro do track-playback. Nenhuma mensagem de subscribe é enviada: o servidor
  envia tudo para o usuário assim que o dispositivo é registrado.

- Mensagens push: `{"type":"message","uri":"hm://...","headers":{...},"payloads":[...]}`.
  Itens de `payloads` são objetos JSON ou strings (base64 protobuf, ou texto JSON simples).
  Nenhuma mensagem na captura usou `Transfer-Encoding: gzip`, mas um cliente deve
  descompactar quando aquele cabeçalho estiver presente ou os bytes começarem com `1f 8b`.
- Nenhum frame `type:"request"` (com resposta) foi visto.

| padrão uri | cabeçalhos | payload | carrega |
|---|---|---|---|
| `hm://pusher/v1/connections/{conn}` | `Spotify-Connection-Id` | nenhum (`method: PUT`) | connection id, uma vez por socket |
| `hm://connect-state/v1/cluster` | `content-type: application/json` | objeto JSON: `cluster`, `update_reason`, `ack_id`, `devices_that_changed` | cluster completo (`active_device_id`, `devices`, `player_state`, `timestamp`, `started_playing_at`, `not_playing_since_timestamp`...). Razões vistas: `DEVICE_STATE_CHANGED`, `DEVICE_VOLUME_CHANGED` |
| `hm://track-playback/v1/command` | `content-type: application/json` | objeto JSON, `type` = `replace_state` (`state_machine`, `state_ref`, `prev_state_ref`, `seek_to`, `selected_alias_id`, `context_metadata`, `registration_token`) ou `set_volume` (`volume`, `command_id`, `registration_token`) | comandos para o player local (estado de máquina de fila de play) |
| `hm://collection/collection/{user}` | `application/octet-stream`, `MC-ETag`, `Collection-Source-Revision`, às vezes `Collection-Update-Id` | base64 protobuf | mudança em Curtidas |
| `hm://collection/collection/{user}/json` | mesmo, `Content-Type: text/plain` | string JSON `{"items":[{"type":"track","unheard","addedAt","removed","identifier","itemData"?}]}` | mesma mudança, forma JSON (mais fácil de usar) |
| `hm://collection/artist/{user}` e `.../json` | mesmo que acima | protobuf / JSON com `type:"artist"` | mudança em artistas seguidos |
| `hm://playlist/v2/playlist/{id}` | nenhum | base64 protobuf: campo 1 URI da playlist, depois bytes de revisão e ops | conteúdo ou atributos da playlist mudaram |
| `hm://playlist/v2/user/{user}/rootlist`, `hm://playlist/user/{user}/rootlist` | nenhum | base64 protobuf: rootlist URI + revisão | lista de playlists da biblioteca mudou (ambas as formas são pushadas) |
| `hm://playlist/v2/list/liked-songs-artist/{artistId}` | nenhum | base64 protobuf: `spotify:list:liked-songs-artist:{id}` | lista de Curtidas por artista mudou |
| `hm://herodotus/uri/spotify:list:play-history:v1/resume-point-revision/{uuid}` | `Content-Type: application/octet-stream` | base64 protobuf: uuid, `spotify:list:play-history:v1`, URI de faixa ou anúncio | entrada de histórico de reprodução adicionada (uma por faixa/anúncio tocada) |

## 5. Connect state e registro de dispositivo

Ordem na inicialização: token + client token -> apresolve -> dealer aberto (obter `{conn}`) ->
registrar dispositivo track-playback -> PUT connect-state -> chamadas pathfinder.

### Registrar o player: `POST {spclient}/track-playback/v1/devices`

```json
{"device": {
   "brand": "spotify", "model": "web_player", "name": "Web Player (Chrome)",
   "device_type": "computer", "device_id": "{dev}",
   "platform_identifier": "web_player linux undefined;chrome 155.0.0.0;desktop",
   "is_group": false, "is_public": false, "metadata": {},
   "correlation_id": "<uuid>", "client_version": "harmony:4.81.1-1f3d48b7f",
   "capabilities": {"change_volume": true, "enable_play_token": true,
     "supports_file_media_type": true, "play_token_lost_behavior": "pause",
     "disable_connect": false, "audio_podcasts": true, "video_playback": true,
     "manifest_formats": ["file_ids_mp3", "file_urls_mp3", "manifest_urls_audio_ad",
       "manifest_ids_video", "file_urls_external", "file_ids_mp4", "file_ids_mp4_dual"],
     "supports_preferred_media_type": true, "supports_playback_offsets": true,
     "supports_playback_speed": true}},
 "connection_id": "{conn}", "client_version": "harmony:4.81.1-1f3d48b7f",
 "outro_endcontent_snooping": false, "volume": 65535}
```

`{dev}` são 40 hex chars, aleatório por carregamento de página. Resposta: `initial_seq_num`,
`device_keep_alive_update_seconds` (21600), `endsongs`, `file_format_filter`,
`supports_observing`, `effective_license` (`tft` em conta gratuita). Desregistrar:
`DELETE track-playback/v1/devices/{dev}` com corpo `seq_num`, `state_ref`, `sub_state`,
`previous_position`, `debug_source: "deregister"`.

### Entrar no connect-state: `PUT {spclient}/connect-state/v1/devices/hobs_{dev35}`

Id do path = `"hobs_" + primeiros 35 chars de {dev}` (40 chars no total). Cabeçalho
`x-spotify-connection-id: {conn}`. Corpo:

```json
{"member_type": "CONNECT_STATE",
 "device": {"device_info": {"capabilities": {"can_be_player": false, "hidden": true,
   "needs_full_player_state": true}}}}
```

A entrada hobs é um observador oculto; o dispositivo de reprodução real é o de track-playback.
Resposta é o cluster JSON (mesmo que GET abaixo). Sair com
`DELETE connect-state/v1/devices/hobs_{dev35}` (204).

### `GET {spclient}/connect-state/v1/cluster`

Cabeçalho `x-cache-hint`. Resposta: `timestamp`, `server_timestamp_ms`,
`active_device_id`, `devices` (mapa `{dev}` -> `can_play`, `volume` (0..65535), `name`,
`capabilities` (`can_be_player`, `is_controllable`, `volume_steps`, `supported_types`,
`command_acks`, `supports_*`...), `device_software_version`, `device_type`, `device_id`,
`client_id`, `brand`, `model`, `license`, `is_same_network`, `public_ip`),
`player_state`, `started_playing_at`, `need_full_player_state`,
`needs_state_updates`, `needs_local_devices`.

### Comandos: `POST {spclient}/connect-state/v1/player/command/from/{dev}/to/{target}`

`from` = próprio dispositivo, `to` = dispositivo alvo (ambos próprio `{dev}` quando tocando localmente).
Corpo `{"command": {"endpoint": "...", "logging_params": {"command_id": "<hex>", ...}, ...}}`.
Resposta `{"ack_id": "..."}`; o efeito chega como um dealer `track-playback/v1/command`
e um `connect-state/v1/cluster` push.

| endpoint | campos extras |
|---|---|
| `play` | `context` (`uri`, `url` = `context://{uri}`, `metadata`), `play_origin` (`feature_identifier`, `feature_version`, `referrer_identifier`), `options` (`license: "tft"`, `skip_to` {`track_index`/...}, `player_options_override`), `logging_params.page_instance_ids`, `interaction_ids` |
| `set_options` | `repeating_context`, `repeating_track` |
| `add_to_queue` | `track` (`uri`, `metadata.is_queued: "true"`, `provider: "queue"`) |

Volume: `PUT connect-state/v1/connect/volume/from/{dev}/to/{target}` corpo
`{"volume": 0..65535}` -> `{"ack_id"}`.

## Lacunas

- Corpo do bundle de entrada principal `web-player.{hash}.js` não capturado: 30 op hashes vêm apenas de
  requisições, e seu tipo `query`/`mutation` e qualquer op nunca chamada do bundle principal são
  desconhecidos. Re-capturá-lo (ou baixá-lo no momento da atualização) para completar a tabela.
- Algoritmo TOTP e segredo por trás de `totp`/`totpVer` não documentados aqui (vivem no
  JS da página); sem eles `/api/token` precisa de um cookie `sp_dc` válido e códigos frescos.
- Se pathfinder ou spclient aceitam uma requisição sem `client-token` não foi testado.
- Formatos de erro: token expirado (401), persisted query desconhecido, fallback do apresolve não
  foram observados.
- Nenhum payload de dealer gzip e nenhum frame `type:"request"` do dealer visto; Connect para
  outro dispositivo (transferência, comandos remotos) não capturado.
- Schemas protobuf de collection, playlist e herodotus pushes são apenas descritos por
  seus campos visíveis.
- Nenhum comando de connect-state foi visto para pause, resume, next ou seek no dispositivo local;
  eles apareceram apenas em `PUT track-playback/v1/devices/{dev}/state` reports (doc de reprodução).
