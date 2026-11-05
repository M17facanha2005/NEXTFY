# Biblioteca e playlists (Web Player)

Notas de sala limpa de capturas de rede do open.spotify.com (conta gratuita, 2026-10-15,
sessões `04-playlists`, `05-playlist-edit`, `06-likes`, mais as mesmas chamadas vistas em outras
sessões). Nada aqui é copiado do código do Spotify; apenas descreve o que passa pela rede.
Placeholders: `{user}` username da conta, `{id}` base62 id, `{likedId}` o id da playlist Curtidas da
conta, `{uuid}` um UUID aleatório do cliente, `{rev}` uma revisão de playlist.
Veja [CAPTURE-MAP.md](CAPTURE-MAP.md) para a configuração da sessão (tokens, pontos de acesso, dealer).

## Formato comum de requisição

### GraphQL (pathfinder)

`POST https://api-partner.spotify.com/pathfinder/v2/query`

Cabeçalhos de requisição (apenas nomes): `accept`, `accept-language`, `app-platform` (`WebPlayer`),
`authorization` (`Bearer` web token), `client-token`, `content-type` (JSON),
`spotify-app-version`, além dos `origin`, `referer`, `user-agent`, `sec-*` do navegador.

```json
{"variables": {...}, "operationName": "<name>",
 "extensions": {"persistedQuery": {"version": 1, "sha256Hash": "<hash>"}}}
```

Resposta é `{"data": {...}}`. Erros não foram capturados; toda chamada nas sessões
retornou HTTP 200. O hash identifica uma persisted query e muda quando o Spotify envia
um novo build da web, então mantenha hashes em uma tabela que pode ser atualizada sem mudanças de código.

| operationName | sha256Hash |
|---|---|
| `profileAttributes` | `08ffb4730af3746e04a8301396f20875dbbce10c75243803091a9274eacc8ac0` |
| `accountAttributes` | `d48062d0df0a256ff78ed9e232b83d238dcd710b371efd304b0e178b62595582` |
| `libraryV3` | `390c78e5b951029bad359785e69b07b536a509c581cbcd0aded5e5067f187455` |
| `areEntitiesInLibrary` | `134337999233cc6fdd6b1e6dbf94841409f04a946c5c7b744b09ba0dfe5a85ed` |
| `isCurated` | `e4ed1f91a2cc5415befedb85acf8671dc1a4bf3ca1a5b945a6386101a22e28a6` |
| `editablePlaylists` | `d5c4b8096437dcc2ac9528c91dfcd299e35b747cda2f8f75d28f41f49c5092ba` |
| `addToLibrary` | `896ebcb47815681340860d121cb5d494e157e2a78d3950385cd54e0393c67148` |
| `removeFromLibrary` | `896ebcb47815681340860d121cb5d494e157e2a78d3950385cd54e0393c67148` (mesmo documento que `addToLibrary`) |
| `applyCurations` | `05b739a3a73091c213385233b9d3ed8a857c2ca29d2eebadb3d04ed12e288697` |
| `addToPlaylist` | `b907ad6b088d5bf7680fa81198996d58398c2d55dfcdf59fcc959e0be98a418a` |
| `fetchPlaylist`, `fetchPlaylistContents`, `fetchPlaylistMetadata` | `21ea11a6ac75a5f3e01fd365d29c18e0a4a8b7dc09f89bbb5d73910190c6acaf` (um documento, três operações) |
| `playlistPermissions` | `e43d1d35f231cf289c23c9d9c489f4a4f502e4eda09839c530608f107b6556b8` |
| `lookupChildEntities` | `91ce02e32b19123de231dc8de91fe4b9ab84eca087d4c015549308d77fbb6d10` |

### spclient REST

Host `spclient.wg.spotify.com` (o Web Player também usa o host `{region}-spclient` do
`apresolve`). Cabeçalhos de requisição (apenas nomes): `accept` (`application/json`),
`accept-language`, `app-platform`, `authorization`, `client-token`, `spotify-app-version`,
e em POST `content-type` (`application/json;charset=UTF-8` para chamadas de playlist,
`application/json` para chamadas de collection). Todos os corpos abaixo são JSON; nada nesta área
usou protobuf sobre HTTP. Apenas os pushes do dealer usam protobuf.

## Conta

### profileAttributes

`variables: {}`. Campos usados: `data.me.profile.username` (este é `{user}` para as
URLs spclient), `uri` (`spotify:user:{user}`), `name` (nome de exibição), `avatar`.

```json
{"data":{"me":{"profile":{"accountId":"…","avatar":null,"avatarBackgroundColor":1697420,
 "name":"…","socialHandle":null,"uri":"spotify:user:{user}","username":"{user}"}}}}
```

### accountAttributes

`variables: {}`. Campos usados: `data.me.account.product` (`FREE`),
`attributes.catalogue` (`free`), `attributes.ads` (true), `attributes.onDemand`,
`country` (duas letras), `features.personalizedRecommendations`,
`attributes.hasPodcastsAvailable`, `attributes.filterExplicitContent`.

```json
{"data":{"me":{"account":{"attributes":{"ads":true,"catalogue":"free","onDemand":true,
 "hasPodcastsAvailable":true,"filterExplicitContent":null,…},
 "country":"CH","features":{"personalizedRecommendations":"PERMITTED"},"product":"FREE"}}}}
```

## Lendo a biblioteca

### libraryV3 (lista Sua Biblioteca)

Chamado em todo carregamento de página e novamente após cada mudança na biblioteca (curtir, salvar, seguir,
criar, renomear). Apenas o request padrão foi capturado:

```json
{"order": null, "textFilter": "", "features": ["LIKED_SONGS","YOUR_EPISODES_V2",
 "PRERELEASES","PRERELEASES_V2","CLIPS","EVENTS"], "limit": 50, "offset": 0,
 "flatten": false, "expandedFolders": [], "folderUri": null,
 "includeFoldersWhenFlattening": true}
```

Paginação: `offset`/`limit`, pare quando `offset >= totalCount`. Busca de texto: `textFilter`.
Pastas: `folderUri` abre uma pasta, `expandedFolders` lista URIs de pastas mostradas inline,
`flatten` lista tudo sem pastas. A conta não tinha pastas, então estes sempre
foram os padrões (veja Gaps).

Resposta `data.me.libraryV3` (`__typename: LibraryPage`):

| Campo | Significado |
|---|---|
| `totalCount`, `pagingInfo {limit, offset}` | paginação |
| `availableFilters[] {id, name}` | ids vistos: `Playlists`, `Artists`, `Albums` (apenas tipos presentes na biblioteca são oferecidos; `name` está localizado) |
| `availableSortOrders[] {id, name}` | ids vistos: `Recents`, `Recently Added`, `Alphabetical`, `Creator` |
| `selectedFilters[]`, `selectedSortOrder {id, name}` | estado atual (padrão `Recents`) |
| `breadcrumbs[]` | caminho da pasta quando dentro de uma pasta |
| `items[]` | `{addedAt {isoString}, depth, item, pinnable, pinned, playedAt {isoString}\|null}` |

`item` é um wrapper com `__typename`, `_uri` e `data`:

| `item.__typename` | campos de `data` usados |
|---|---|
| `LibraryPseudoPlaylistResponseWrapper` (`spotify:collection:tracks`) | `name`, `count` (número de faixas curtidas), `image.sources[]` |
| `PlaylistResponseWrapper` | `name`, `description`, `uri`, `format`, `images.items[].sources[]`, `ownerV2.data {name, username, uri}`, `currentUserCapabilities {canEditItems, canView}`, `revisionId`, `attributes[] {key, value}` |
| `AlbumResponseWrapper` | `name`, `uri`, `type` (`ALBUM`, `SINGLE`…), `artists.items[] {profile.name, uri}`, `coverArt.sources[]`, `date {isoString, precision}`, `playability {playable, reason}` |
| `ArtistResponseWrapper` | `profile.name`, `uri`, `visuals.avatarImage.sources[]` |

`sources[]` das imagens são `{url, width, height}` (width/height podem ser null). A maioria das entradas também
carrega `extractedColors.colorDark.hex`.

```json
{"addedAt":{"isoString":"2026-10-15T21:14:42.278Z"},"depth":0,"pinnable":true,"pinned":false,
 "playedAt":null,"item":{"__typename":"PlaylistResponseWrapper","_uri":"spotify:playlist:{id}",
 "data":{"__typename":"Playlist","name":"NEXTFY capture test","description":"","format":"",
 "currentUserCapabilities":{"canEditItems":true,"canView":true},
 "images":{"items":[{"sources":[{"url":"https://i.scdn.co/image/…","width":null,"height":null}]}]},
 "ownerV2":{"data":{"__typename":"User","name":"…","uri":"spotify:user:{user}","username":"{user}"}},
 "revisionId":"AAAAA8Zc…","uri":"spotify:playlist:{id}"}}}
```

A entrada Curtidas está sempre primeiro e `pinned: true`.

### playlist/v2/user/{user}/rootlist (playlists seguidas e próprias)

`GET https://spclient.wg.spotify.com/playlist/v2/user/{user}/rootlist?decorate=revision,length,attributes,timestamp,owner,capabilities&bustCache={ms}`

Com `accept: application/json` a resposta é JSON, não protobuf. Após uma mudança o
Web Player re-lê com `&revision={n},{hash}` em vez de `bustCache` (veja Revisões).

```json
{"revision":"AAAAA6N1…","length":1,"attributes":{},"timestamp":"1792098820089",
 "contents":{"pos":0,"truncated":false,
   "items":[{"uri":"spotify:playlist:{id}","attributes":{"timestamp":"1792098882278","public":true}}],
   "metaItems":[{"revision":"AAAAAcQB…","length":0,"timestamp":"1792098882241",
    "ownerUsername":"{user}","attributes":{"name":"Ma playlist n° 2","description":"…",
    "format":"…","formatAttributes":[{"key":"…","value":"…"}],"pictureSize":[…]},
    "capabilities":{"canView":true,"canEditItems":true,"canEditMetadata":true,
     "canAdministratePermissions":true,"canDelete":…,"listAttributeCapabilities":{…},…}}]}}
```

`items[i]` e `metaItems[i]` correspondem por índice. Um rootlist vazio tem `length: 0` e sem
`items`. Apenas playlists vivem aqui; álbuns e artistas vêm da collection
(`libraryV3`). Pastas não estavam presentes (veja Gaps).

### areEntitiesInLibrary

`variables: {"uris": ["spotify:track:{id}", "spotify:album:{id}", "spotify:artist:{id}", "spotify:show:{id}", "spotify:episode:{id}", …]}`
(tipos mistos, até cerca de 30 por chamada).

Resposta `data.lookup[]`, mesma ordem de `uris`:

```json
{"data":{"lookup":[{"__typename":"TrackResponseWrapper","data":{"__typename":"Track","saved":false}},
 {"__typename":"AlbumResponseWrapper","data":{"__typename":"Album","saved":false}},
 {"__typename":"ArtistResponseWrapper","data":{"__typename":"Artist","saved":false}},
 {"__typename":"PodcastOrAudiobookResponseWrapper","data":{"__typename":"Audiobook","saved":false}}]}}
```

Use `saved` para o botão Salvar do álbum e o botão Seguir do artista. Para faixas o coração
segue `isCurated` (próxima seção); `saved` nunca voltou true nas capturas.

### isCurated (coração da faixa)

`variables: {"uris": ["spotify:track:{id}", …]}` (também enviado com URIs de episódio).
Resposta `data.lookup[].data.isCurated` (bool), mesma ordem. True significa que a faixa está em
Curtidas (visto true para a faixa curtida, false após descurtir).

### collection/v2/contains

`POST https://spclient.wg.spotify.com/collection/v2/contains?market=from_token`

```json
{"username":"{user}","set":"artistban","items":[{"uri":"spotify:artist:{id}"},{"uri":"spotify:artist:{id}"}]}
```
```json
{"found":[false,false]}
```

Sets vistos: `artistban` (artista bloqueado por "Não tocar este artista", verificado para cada
artista na tela) e `ignoreinrecs` (faixa excluída do perfil de gosto, verificada para a
faixa cujo menu está aberto). Este endpoint não foi usado para curtidas ou salvos.

### editablePlaylists (a tela "Adicionar à playlist" / coração)

`variables: {"offset":0,"limit":50,"textFilter":"","uris":["spotify:track:{id}"]}`

Resposta `data.me.editablePlaylists` (`EditablePlaylistPage`): `totalCount`,
`pagingInfo`, `items[] {curates, pinned, item}`. `item` usa os mesmos wrappers que
`libraryV3` (`LibraryPseudoPlaylistResponseWrapper` para Curtidas, depois
`PlaylistResponseWrapper` com `name`, `uri`, `images`). `curates` é true quando a
faixa dada já está naquela lista. Apenas playlists que o usuário pode editar são listadas.

```json
{"data":{"me":{"editablePlaylists":{"__typename":"EditablePlaylistPage","totalCount":2,
 "pagingInfo":{"limit":50,"offset":0},"items":[
 {"curates":true,"pinned":true,"item":{"__typename":"LibraryPseudoPlaylistResponseWrapper",
  "_uri":"spotify:collection:tracks","data":{"name":"…","uri":"spotify:collection:tracks","image":{…}}}},
 {"curates":false,"pinned":false,"item":{"__typename":"PlaylistResponseWrapper",
  "_uri":"spotify:playlist:{id}","data":{"name":"NEXTFY capture test","uri":"spotify:playlist:{id}","images":{…}}}}]}}}}
```

### Curtidas

A página Curtidas (`/collection/tracks`) agora é uma playlist real pertencente ao usuário:
`spotify:playlist:{likedId}` com `format: "liked-songs"`. O id é por conta
(base62, no intervalo `37i9dQZF1F…` de ids gerados pelo Spotify). Suas faixas carregam com
as operações normais de playlist:

- `fetchPlaylist` `{"uri":"spotify:playlist:{likedId}","offset":0,"limit":25,"enableWatchFeedEntrypoint":true,"includeEpisodeContentRatingsV2":true}`
- `fetchPlaylistContents` `{"uri":…,"offset":25,"limit":50,"includeEpisodeContentRatingsV2":true}` para as próximas páginas
- `playlistPermissions` `{"uri":…}` (Curtidas: `basePermission: CONTRIBUTOR`, `canEditItems: true`, `canEditMetadata: false`)

Resposta `fetchPlaylist` `data.playlistV2`: `name`, `format`, `ownerV2`, `revisionId`,
`images`, `currentUserCapabilities`, `content {totalCount, pagingInfo, items[]}`.
Cada item: `uid` (string hex, identifica a linha), `addedAt.isoString`, `addedBy`,
`itemV2.data` (Track: `uri`, `name`, `artists.items[]`, `albumOfTrack {uri, name, coverArt}`,
`trackDuration.totalMilliseconds`, `playability`, `contentRating.label`), `itemV3`
(nova forma baseada em traços da mesma entidade).

`{likedId}` também aparece na resposta de `POST spclient/extended-metadata/v0/extended-metadata`
com `extensionKind: 246` (`spotify.contentagnostic.v2.CurationExperienceTrait`), a primeira
resposta de extensão após o id já ser conhecido (veja "Encontrando o id das Curtidas"). Decodificado
protobuf dessa extensão (de uma requisição de faixa em outra sessão):

```
1: "spotify:track:{id}"
2 { 2 { 1: "spotify:playlist:{likedId}" } }      target padrão = Curtidas
3 { 4 { 1: "spotify:playlist:…"  1: "spotify:playlist:…" } }   outros targets
```

O mesmo `{likedId}` é usado como `contextUri` por `applyCurations` e em pushes do dealer.
`libraryV3` e `editablePlaylists` ainda o nomeiam `spotify:collection:tracks`. Cache
`{likedId}` uma vez encontrado.

### Encontrando o id das Curtidas

Verificado na captura de início a frio `01-login` (login, depois primeiro carregamento do Web Player).

- Primeira ocorrência na rede é a requisição de `lookupChildEntities`
  (`variables.uris: ["spotify:playlist:{likedId}"]`). O cliente já tinha o id quando
  enviou aquela requisição, então a fonte não está na captura. A resposta repete em
  `data.lookupEntities[0].uri`. Nenhuma requisição ou resposta anterior carrega, em base62, hex,
  raw 16-byte gid ou base64. Frames do dealer e registros de navegação também não.
- Não presente em `profileAttributes`, `accountAttributes`, `libraryV3` (nomeia Curtidas
  `spotify:collection:tracks`), `home`, `feedBaselineLookup`, o rootlist, ou páginas HTML.
- A primeira resposta com ele é `extended-metadata` (`extensionKind 246`), cerca de 50 ms depois.
  Lá está na lista de outros-targets (CurationExperienceTrait campo 3, `4 { 1 }`). Essa
  requisição foi para episódios, e seu target padrão é uma playlist diferente, então não assuma
  que o padrão é Curtidas.
- Não capturado: o corpo de `POST accounts.spotify.com/login/code/verify` (vazio no arquivo)
  e cerca de 90 respostas estáticas ou redirecionamento sem corpo armazenado. A verdadeira fonte é uma
  daquelas, ou armazenamento do navegador, que a captura de rede não pode mostrar.

Fontes de fallback, em ordem:

1. `{likedId}` salvo de uma sessão anterior da mesma conta.
2. `extended-metadata` `extensionKind 246` para qualquer faixa ou episódio (target padrão ou
   outros targets, veja o decode acima).
3. Um push do dealer em `hm://playlist/v2/playlist/{likedId}` (campo 1 do payload), após a
   primeira mudança de curtida.

## Editando a biblioteca

Todas as mutações responderam HTTP 200 com um payload tipado vazio. A UI então re-executou
`libraryV3` e confiou nos pushes do dealer para o resto.

### addToLibrary (curtir uma faixa, salvar um álbum, seguir um artista)

```json
{"variables":{"libraryItemUris":["spotify:track:{id}"],"interactionId":"{uuid}"},
 "operationName":"addToLibrary","extensions":{…}}
```
`interactionId` é um UUID fresco (enviado como `null` por alguns botões). A mesma chamada foi
usada para `spotify:track:`, `spotify:album:` e `spotify:artist:`.

Resposta: `{"data":{"addLibraryItems":{"__typename":"AddLibraryItemsResponse"}}}`

### removeFromLibrary (desalvar álbum, deixar de seguir artista, descurtir da barra do player)

```json
{"variables":{"libraryItemUris":["spotify:album:{id}"]},"operationName":"removeFromLibrary",…}
```
Resposta: `{"data":{"removeLibraryItems":{"__typename":"RemoveLibraryItemsResponse"}}}`.
Também visto para `spotify:track:` (descurtir do coração do now-playing, sessão 08).

### applyCurations (descurtir de uma linha)

Clicar no coração cheio em uma linha abre a tela (`editablePlaylists`), desmarcando
Curtidas envia:

```json
{"variables":{"input":{"itemUris":["spotify:track:{id}"],
 "curations":[{"contextUri":"spotify:playlist:{likedId}","curationType":"UNCURATE"}]}},
 "operationName":"applyCurations",…}
```
```json
{"data":{"applyCurations":[{"__typename":"TrackResponseWrapper","data":{"__typename":"Track","isCurated":false}}]}}
```
Apenas `UNCURATE` foi capturado. Várias entradas `curations` (uma por playlist marcada ou
desmarcada) são permitidas pelo formato. Para um descurtir simples, `removeFromLibrary` com a
URI da faixa faz o mesmo trabalho.

## Editando playlists

### Revisões

Uma revisão é base64 de 24 bytes: contador de 4 bytes big-endian então um hash de 20 bytes.
Exemplo `AAAAAU2+Oz2u…` = contador 1, hash `4dbe3b3d…`. O parâmetro de consulta `revision=`
usa a forma decodificada `{counter},{hash hex}`.

Respostas de mudança compartilham uma forma:

```json
{"revision":"{rev}","syncResult":{"fromRevision":"{rev}","toRevision":"{rev}"},
 "resultingRevisions":["{rev}"],"multipleHeads":false,"changesRequireResync":false}
```
`playlist/{id}/changes` também retorna `capabilities` (mesmo objeto que em `metaItems` do
rootlist).

### Criar uma playlist

1. `POST https://spclient.wg.spotify.com/playlist/v2/playlist`
   ```json
   {"ops":[{"kind":"UPDATE_LIST_ATTRIBUTES",
     "updateListAttributes":{"newAttributes":{"values":{"name":"Ma playlist n° 2"}}}}]}
   ```
   Resposta: `{"uri":"spotify:playlist:{id}","revision":"{rev}"}`
2. `POST https://spclient.wg.spotify.com/playlist/v2/user/{user}/rootlist/changes` (ADD abaixo,
   `changesRequireResync: true` neste caso)
3. `GET rootlist?…&revision={n},{hash}`, então `libraryV3`, `playlistPermissions`,
   `fetchPlaylist` no novo URI.

O nome padrão é escolhido pelo cliente (localizado "My Playlist #N").

### Renomear (e outros atributos de lista)

`POST https://spclient.wg.spotify.com/playlist/v2/playlist/{id}/changes`

```json
{"deltas":[{"ops":[{"kind":"UPDATE_LIST_ATTRIBUTES",
  "updateListAttributes":{"newAttributes":{"values":{"name":"NEXTFY capture test"}}}}],
  "info":{"source":{"client":"WEBPLAYER"}}}]}
```
Nenhuma revisão base foi enviada. A UI então re-executou `fetchPlaylist` e `libraryV3`.
Descrição é presumivelmente `values.description` (não capturado).

### Seguir / deixar de seguir uma playlist (mudanças do rootlist)

`POST https://spclient.wg.spotify.com/playlist/v2/user/{user}/rootlist/changes`

Seguir (também usado após criar):
```json
{"deltas":[{"ops":[{"kind":"ADD","add":{"items":[{"uri":"spotify:playlist:{id}",
 "attributes":{"timestamp":"1792098815150"}}],"addFirst":true}}],
 "info":{"source":{"client":"WEBPLAYER"}}}]}
```
`timestamp` é o tempo do cliente em ms, como string.

Deixar de seguir:
```json
{"deltas":[{"ops":[{"kind":"REM","rem":{"items":[{"uri":"spotify:playlist:{id}"}],
 "itemsAsKey":true}}],"info":{"source":{"client":"WEBPLAYER"}}}]}
```
`itemsAsKey: true` remove por URI, sem índice necessário. Após cada chamada: `GET rootlist`
com a nova revisão, então `libraryV3` duas vezes.

### addToPlaylist

```json
{"variables":{"playlistUri":"spotify:playlist:{id}","playlistItemUris":["spotify:track:{id}"],
 "newPosition":{"moveType":"BOTTOM_OF_PLAYLIST","fromUid":null},"interactionId":"{uuid}"},
 "operationName":"addToPlaylist",…}
```
Resposta: `{"data":{"addItemsToPlaylist":{"__typename":"AddItemsToPlaylistPayload"}}}`.
Apenas `BOTTOM_OF_PLAYLIST` foi visto; `fromUid` sugere outros tipos de movimento posicionados
em relação a uma linha `uid` (não capturado). A UI então re-executou `fetchPlaylistMetadata`.

### playlistextender/extendp (Músicas recomendadas abaixo de uma playlist própria)

`POST https://spclient.wg.spotify.com/playlistextender/extendp/`

```json
{"playlistURI":"spotify:playlist:{id}","trackSkipIDs":[],"numResults":20}
```
Resposta:
```json
{"recommendedTracks":[{"id":"6U9VpmP3dEinuezxCJ2Zg9","originalId":"spotify:track:6U9VpmP3dEinuezxCJ2Zg9",
 "name":"Ain't In LA","artists":[{"id":"2qanRMyA5bNuTvz1dK45OP","name":"ADÉLA"}],
 "album":{"id":"2yDFVH9CeOHt0sc9eI0aBs","name":"PRIMA","imageUrl":"https://i.scdn.co/image/…","largeImageUrl":"https://i.scdn.co/image/…"},
 "duration":184927,"explicit":true,"popularity":91,"score":98.0,"contentRating":[]}, …],
 "request":{"playlistURI":"spotify:user:{user}:playlist:{id}","trackIDs":[],"artistIDs":[],…},
 "details":{"playlistLoad":8.0,"totalTime":43.0}}
```
`trackSkipIDs` provavelmente contém ids já mostrados ou dispensados (para "Atualizar").
Adicionar um deles usa `addToPlaylist`.

## Pushes do dealer

No dealer WebSocket (veja CAPTURE-MAP) cada mensagem é
`{"type":"message","uri":"hm://…","headers":{…},"payloads":["<base64 ou texto>"]}`.
Eles chegaram antes da resposta HTTP da mutação que os causou, e também vêm de
mudanças feitas em outros dispositivos, então use-os para atualizar caches.

### Collection (curtidas, álbuns salvos, artistas seguidos)

| uri | Content-Type | payload |
|---|---|---|
| `hm://collection/collection/{user}` | `application/octet-stream` | protobuf, faixas e álbuns |
| `hm://collection/collection/{user}/json` | `text/plain` | JSON, mesma mudança |
| `hm://collection/artist/{user}` | `application/octet-stream` | protobuf, artistas |
| `hm://collection/artist/{user}/json` | `text/plain` | JSON, mesma mudança |

Cabeçalhos: `MC-ETag` (nova versão da collection), `Collection-Source-Revision` (revisão anterior),
às vezes `Collection-Update-Id`.

Forma JSON (mais fácil de usar):
```json
{"items":[{"type":"track","unheard":false,"addedAt":1792099111,"removed":false,
 "identifier":"4EoJ151oQ5jY48z4RhSE96","itemData":{"saveSource":{"interactionId":"{uuid}"}}}]}
```
`type` visto: `track`, `album`, `artist`. Remoção: `"removed":true,"addedAt":0`.

Forma protobuf, números de campo:
```
1 (item, repetido) {
  1: type      varint: 0 = track, 1 = album, 4 = artist
  2: gid       16 bytes (id base62 decodificado)
  5: addedAt   varint, unix seconds (0 em remoção)
  6: removed   varint 0/1
  8: itemData { 1 { 2: interactionId string } }
}
```

### Playlists

| uri | Quando |
|---|---|
| `hm://playlist/v2/user/{user}/rootlist` e `hm://playlist/user/{user}/rootlist` | rootlist mudou (seguir, deixar de seguir, criar) |
| `hm://playlist/v2/playlist/{id}` | uma playlist mudou (itens adicionados, também visto ao apenas abrir uma playlist editorial) |
| `hm://playlist/v2/playlist/{likedId}` | Curtidas mudou (curtir/descurtir) |
| `hm://playlist/v2/list/liked-songs-artist/{artistId}` | a lista de Curtidas por artista desse artista mudou |

Payload é base64 protobuf:
```
1: uri               "spotify:user:{user}:rootlist" / "spotify:playlist:{id}" / "spotify:list:liked-songs-artist:{artistId}"
2: nova revisão      24 bytes (contador + hash, como acima)
3: revisão anterior  24 bytes (quando presente)
4: ops (repetido)    ex. kind 2 = add:
     { 1: 2  2 { 1: fromIndex  2 { 1: "spotify:track:{id}"
                                   2 { 1: addedBy "{user}"  2: timestamp ms  17 { 2: interactionId } } } } }
5: { 1: segundos  2: nanos }   hora da mudança
6: string                     nome do servidor
9: varint                     0 ou 1
```
Rootlist e pushes de liked-songs-artist carregaram apenas campos 1, 2 e 9. Regra mais simples do cliente:
em qualquer push de playlist, re-busque aquela playlist (ou o rootlist / `libraryV3`) a menos
que a revisão seja igual à já guardada.

## Sair de uma playlist colaborativa

O menu da playlist do Web Player mostra "Sair da playlist" apenas quando a
`currentUserCapabilities.canCancelMembership` da playlist é verdadeira (um membro que não é o proprietário; mesma flag
em `fetchPlaylist` e no item de permissão do rootlist). A entrada abre um
diálogo de confirmação (`leave-playlist.dialog.*`: título, uma descrição que depende de privado / público
contribuinte / público ouvinte, Cancelar, Sair). Lido do bundle (`xpui-root-dialogs`, módulo
`PlaylistPermissionsAPI`; não enviado em uma conta ao vivo ainda):

| Etapa | Chamada |
|---|---|
| playlist privada apenas (permissão base `BLOCKED`) e na biblioteca | `RootlistAPI.remove([{uri}])` (a playlist sai da biblioteca) |
| a saída em si | serviço de página `PlaylistPermissionsAPI.removeMember(playlistUri, ownUsername)` |
| depois | o diálogo fecha e a página navega para `/` |

`removeMember` envia spclient `DELETE /playlist/{id}/permission/member/user/{username}` (JSON content
type, sem corpo, sem market; endpoint id `/playlist/{id}/permission/member/user/{username}`). O
username é o próprio da conta (`UserAPI.getUser().username`); a mesma chamada, com o username de outra pessoa,
é a opção "Remover da playlist" do proprietário na lista de membros. Uma playlist pública permanece na
biblioteca após sair (seguida, somente leitura); para participar novamente um novo link de convite é necessário
(`claimPermissions`). No app: `Playlists.leave(uri)`, oferecido no menu da playlist quando
`Playlist.canLeave`. Convidados também recebem "Sair da playlist" na própria linha da tela de membros.

## Gerenciar membros e parar uma colaboração (proprietário)

A lista "Compartilhado com" do Web Player (`xpui-root-dialogs`, strings `permissions.*`) tem um menu por
membro, controlado por `capabilities.canAdministratePermissions`:

| Entrada do menu | Chamada |
|---|---|
| "Tornar colaborador" (linha de ouvinte) / "Tornar ouvinte" (linha de colaborador) | serviço de página `PlaylistPermissionsAPI.setMemberPermission(playlistUri, username, "CONTRIBUTOR" \| "VIEWER")` |
| "Remover da playlist" | `PlaylistPermissionsAPI.removeMember(playlistUri, username)` |

`setMemberPermission` primeiro lê `GET /playlist/{id}/permission/members` (`{memberPermissions:{username:{revision}}}`)
e falha quando o usuário não é membro, então envia spclient `POST /playlist/{id}/permission/member/user/{username}`
com `{revision, permissionLevel}`. No app: `Playlists.setMemberCollaborator`, `Playlists.removeMember`
(a tela de membros; o menu mostra em cada linha não-proprietária para o proprietário).

O Web Player **não tem "parar colaboração"** (o switch "Convidar como colaborador" do diálogo de compartilhamento apenas
adiciona o token `pt` ao link copiado), e **não há chamada para revogar um link de convite**: as únicas
chamadas `permission-grant` nos bundles são `POST …/permission-grant` (criar, `ttlMs` 7 dias) e
`POST …/permission-grant/token/{pt}/claim`. Então `Playlists.stopCollaboration(uri)` é construído a partir das chamadas de
membro: cada colaborador se torna um ouvinte quando a playlist é privada (não listada no perfil) ou é
removido quando é pública, então a permissão base volta para `BLOCKED` (privado) / `VIEWER` (público).
Links já compartilhados não são revogados; a confirmação do app diz isso. Não testado em uma conta real.

## Blend ("Mix à plusieurs")

O Web Player tem rotas de Blend (`/blend/invitation`, `/blend/taste-match/{id}`), todas em um
único serviço spclient, `https://spclient.wg.spotify.com/blend-invitation` (sem operação GraphQL,
sem serviço de página). Verificado ao vivo na conta de teste gratuita (token e client-token como para qualquer
chamada spclient):

| Chamada | Resposta |
|---|---|
| `POST /v1/generate` (corpo vazio) | `201 {"invite":"https://open.spotify.com/blend/taste-match/{id}?si=…&fallback=getapp&blendDecoration=…"}` |
| `GET /v3/view-invitation/{id}` | `200 {page_type, members, recipient{name,username,image_url,hash}, playlist_uri, title, subtitle, members_title, button_text, footnote}` |
| `PUT /v2/join/{id}` (corpo vazio) | `200 {"playlist_uri":"spotify:playlist:37i9dQZF1E…"}` |

`page_type`: `PENDING_INVITATION` (convite próprio, ninguém entrou), `READY_TO_JOIN_EMPTY_BLEND` /
`READY_TO_JOIN_ALREADY_CREATED_BLEND` (de outra pessoa, aberto), `MAX_MEMBERS`, `ALREADY_JOINED`
(depois `playlist_uri` é definido), `DELETED`. A playlist Blend só existe uma vez que uma segunda conta
tenha entrado: entrar no próprio convite retornou um `playlist_uri` que `fetchPlaylist` responde
"não encontrado" e que fica no rootlist como um item `placeholder` (removido novamente com
`RootlistAPI.remove`). Um convite não pode ser revogado. Um Blend real é uma playlist comum
(`format: "blend"` em `fetchPlaylist`), aberta, tocada e listada como qualquer outra. Um link
`open.spotify.com/blend/taste-match/{id}` aberto no app mostra o convite (ver, depois entrar).

A página do Blend em si (membros, match de gosto, renomear, sair) usa chamadas que o Web Player não tem
uma tela para; são as que os apps móveis do Spotify fazem para aquela página. As duas leituras foram
verificadas ao vivo em um Blend real de dois membros (21/10), e renomear; sair não foi enviado ainda:

| Chamada | Resposta |
|---|---|
| `GET /v3/get-invitation/{playlistId}` | `200 {title, button_text, invitation_link, members[{name, image_url, hash, username}]}` (`image_url` pode ser `""`) |
| `GET /v3/data-stories/{playlistId}` | `200 {intro_story{title1{text,text_color}, subtitle1, background_color, audio_uri}, stories[{story_type, title, subtitle, body, image, background_color, button{navigation_uri}}]}` |
| `PUT blend-writer/v1/rename/{playlistId}` `{"name":…}` | `200`, corpo vazio (verificado com o próprio nome do Blend) |
| `PUT /v1/leave` `{"playlist_uri":"spotify:playlist:…"}` | não enviado ainda |

`blend-writer` é um irmão de `blend-invitation` no mesmo host. Os textos (títulos, palavras dos botões,
linhas de história) vêm traduzidos no idioma da conta.

## Gaps (não capturado)

- Remover faixas de uma playlist: GraphQL `removeFromPlaylist {playlistUri, uids}` remove
  exatamente as linhas nomeadas por `uid` (verificado ao vivo: a mesma música duas vezes tem dois uids, remover
  uma deixa a outra). Nunca enderece uma linha pelo uri da faixa.
- Reordenar faixas (`MOV`, ou `addToPlaylist`/move com `fromUid`; desconhecido).
- Deletar uma playlist (para uma playlist própria o Web Player provavelmente faz apenas o `REM` do rootlist;
  não confirmado).
- Pastas: criar, renomear, mover itens para pastas, marcadores de pasta do rootlist, e
  `libraryV3` com `folderUri`/`expandedFolders`/`flatten` definidos.
- `libraryV3` com um filtro ou outra ordenação: os nomes das variáveis para o filtro
  e ordenação selecionados (`order` é null por padrão, nenhuma chave de filtro foi enviada) não são conhecidos.
- Prender / desprender na Sua Biblioteca.
- Mudar descrição, imagem de capa, público/privado, colaborativo.
- `applyCurations` com `CURATE` (adicionar através da tela de coração), curatela de múltiplas playlists.
- Curtidas para podcasts (`YOUR_EPISODES_V2`), shows, audiolivros.
- Respostas de erro (taxa de conflito de limite, revisão, permissão negada).

## TODO

- TODO: `libraryV3` filtros e ordenações — capturar uma sessão com um chip de filtro e cada ordem de ordenação para obter os nomes das variáveis.
- TODO: pastas do rootlist — capturar criação/movimento de pasta para ver marcadores de pasta nos `items` do rootlist e nos ops `changes`.
