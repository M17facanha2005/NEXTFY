# Busca, navegação e home (Web Player, GraphQL)

Espec de sala limpa de capturas `01-login`, `03-search`, `04-playlists` (conta gratuita,
Web Player `1.3.6.x`, locale `fr`, 2026-10-15). Os ids abaixo são exemplos ou `{id}`;
nenhum token, cookie ou valor de conta é reproduzido.

## Transporte

Todas as operações: `POST https://api-partner.spotify.com/pathfinder/v2/query`, corpo:

```json
{"variables":{...},"operationName":"searchTracks",
 "extensions":{"persistedQuery":{"version":1,"sha256Hash":"<hash>"}}}
```

| Cabeçalho | Contém |
|---|---|
| `authorization` | `Bearer {web access token}` (de `open.spotify.com/api/token`) |
| `client-token` | token de `clienttoken.spotify.com/v1/clienttoken` |
| `app-platform` | `WebPlayer` |
| `spotify-app-version` | build do web player, ex. `1.3.6.1858.g{hash}` |
| `content-type` | `application/json;charset=UTF-8` |
| `accept` | `application/json` |
| `accept-language` | UI locale (`fr`); guia todos os rótulos (títulos, saudação, chips) |
| `origin` / `referer` | `https://open.spotify.com` / `https://open.spotify.com/` |

Resposta: `{"data":{...},"extensions":{"requestIds":{...}}}`. Toda entidade vem como um
`*ResponseWrapper {__typename, data:{__typename, ...}}`; `data.__typename` pode ser
`NotFound` (visto na home) ou um tipo restrito, então sempre faça switch nele.

## Tabela de operações

| operationName | sha256Hash | Gatilho |
|---|---|---|
| `recentSearches` | `ece96d7b82c820fb9938dd1df50dee3e53737d808c3883e3d01c467c423601b0` | abrir página de busca |
| `searchSuggestions` | `f244254b94c0e824d458ac216e0f8406a73f2f69a9ff52831a2f78fa774ff6ce` | cada tecla (debounced) |
| `searchTopResultsList` | `dd78eaff943eba629ed70ee25517b9cea0dcaa41193e2592ae2727660b21892c` | enviar, aba "Todos" |
| `searchTracks` | `b02683192a98dde7966b5e6655a79eeb62713eab703eda9902c932818dd52751` | aba Músicas |
| `searchArtists` | `7bf95d754fdbe32c8b161fbbe54d1ae50974900df4dce4c8f1afcbcad153224d` | aba Artistas |
| `searchAlbums` | `202cb3305e31e5a0767ba7925f28bd728cf8f8b0217e6da43909056071cd70e9` | aba Álbuns |
| `searchPlaylists` | `d520014e748f9ea44f7707d8df1819867ac1205e8b7f3e28f22fe5fc858921b1` | aba Playlists |
| `browseAll` | `dbd8b55e09a58afc52eab438bc228ba28fd72ac2f2148c6c26354980e4579001` | página de busca, consulta vazia |
| `home` | `5fb7da7a03ee2776c0670856609cb85a189a3cecc36cc160b91cde79063b9cf1` | página home |
| `feedBaselineLookup` | `318190494c436b51ab995367bffc66dcdb49dfa023792c60ac15dcb6dfbec42f` | home, após/com `home` |
| `lookupChildEntities` | `91ce02e32b19123de231dc8de91fe4b9ab84eca087d4c015549308d77fbb6d10` | home "Recents" arte filha |
| `fetchEntitiesForRecentlyPlayed` | `cf5d2e94ffd82788470788ae1f6090cc3e9e774fb8fd383580634c6e6f50f7be` | boot do app (contexto de player restaurado) |
| `decorateContextTracks` | `383de00240775c39a6afe0b1055dc562b2a3930894201f9762f3fc32a74971c7` | boot do app (fila de contexto restaurado) |
| *(nenhum)* `pathfinder/v1/query` | `f952da037440f694cc6925b9e3f649d39077a744c4db7dfba01cb883723f4f77` | boot do app, junto com os acima |

Não capturado: abas de busca de podcasts/episódios/usuários/gêneros/audiolivros, página de categoria/gênero,
paginação de seção da home, filtro de chip da home. Veja Gaps.

## Busca

Rotas UI: `/search` -> `/search/{q}` -> `/search/{q}/tracks|artists|albums|playlists`.

### Variáveis

| Op | Variáveis (observadas) |
|---|---|
| `recentSearches` | `limit:Int=50, includeAuthors:Bool=true, includeEpisodeContentRatingsV2:Bool=true` |
| `searchSuggestions` | `query:String, limit:30, numberOfTopResults:30, offset:0, includeAuthors:true, includeAlbumPreReleases:false, includeEpisodeContentRatingsV2:true` |
| `searchTopResultsList` | `query:String, limit:50, offset:0, numberOfTopResults:50, includeArtistHasConcertsField:false, includeAudiobooks:true, includeAuthors:true, includePreReleases:true, includeAlbumPreReleases:false, includeEpisodeContentRatingsV2:true, isPrefix:null, sectionFilters:["GENERIC","VIDEO_CONTENT"]` |
| `searchTracks` | `searchTerm:String, offset:0, limit:20, numberOfTopResults:20, includeAudiobooks:true, includeAuthors:true, includePreReleases:false, includeAlbumPreReleases:false, includeEpisodeContentRatingsV2:true` |
| `searchArtists` / `searchAlbums` / `searchPlaylists` | mesma que `searchTracks` mas `limit:30` |

Nota: o nome é `query` para sugestões/top results e `searchTerm` para as operações de aba.

### Caminhos de resposta

| Op | Raiz | Itens |
|---|---|---|
| `recentSearches` | `data.recentSearches.recentSearchesItems.items[]` (`RecentSearchesPage`) | wrappers diretamente (`ArtistResponseWrapper`, ...) |
| `searchSuggestions` | `data.searchV2.topResultsV2.itemsV2[]` (`TopResultHit`) | `.item` = wrapper |
| `searchTopResultsList` | `data.searchV2.topResultsV2.itemsV2[]` | `.item` = wrapper ou `SearchSectionEntity`; `.matchedFields[]` |
| `searchTracks` | `data.searchV2.tracksV2` | `items[].item` = `TrackResponseWrapper` (nível `item` extra!) |
| `searchArtists` | `data.searchV2.artists` | `items[]` = `ArtistResponseWrapper` |
| `searchAlbums` | `data.searchV2.albumsV2` | `items[]` = `AlbumResponseWrapper` |
| `searchPlaylists` | `data.searchV2.playlists` | `items[]` = `PlaylistResponseWrapper` |

Paginação da aba: `{totalCount, pagingInfo:{limit, nextOffset}}` ao lado de `items`; solicite
a próxima página com `offset = nextOffset` (presumivelmente null na última página).

Tipos de item de `searchSuggestions`: `SearchAutoCompleteEntity` (`item.text` diretamente no item, sem nível `data`; `uri =
spotify:search:{text}`; a UI mostra estes primeiro como completamentos de consulta),
`ArtistResponseWrapper`, `TrackResponseWrapper`, `PlaylistResponseWrapper`,
`EpisodeResponseWrapper` (também `AlbumResponseWrapper` nos top results).

Extras de `searchTopResultsList`:
- `searchV2.{tracksV2,artists,albumsV2,playlists,episodes,podcasts,audiobooks,users,genres,authors}.totalCount` (apenas contagens, sem itens).
- `searchV2.chipOrder.items[].typeName`: ordem das abas, ex. `TRACKS, VIDEOS, ARTISTS, EPISODES, PLAYLISTS, ALBUMS, USERS, PODCASTS, AUDIOBOOKS, GENRES`.
- O primeiro hit é o card "top result" (aqui o artista). Hits se intercalam
  `SearchSectionEntity` shelves: `data:{title.transformedLabel, sectionType:"GENERIC"|"VIDEO_CONTENT", attributionUri, items:[wrappers]}` (ex. "With {artist}" playlists, "Videos" tracks).

### Campos de entidade que a UI precisa (dentro de `wrapper.data`)

| Tipo | Campos |
|---|---|
| `Track` | `uri, id, name, duration.totalMilliseconds, contentRating.label (EXPLICIT/NONE), playability.{playable,reason}, trackMediaType (AUDIO), artists.items[].{uri,profile.name}, albumOfTrack.{uri,id,name,coverArt.sources[]}, associationsV3.videoAssociations.totalCount` |
| `Artist` | `uri, profile.name, visuals.avatarImage.sources[]` (pode ser null), `visuals.avatarImage.extractedColors.colorDark.hex` |
| `Album` | `uri, name, type (ALBUM/SINGLE/EP...), date.year, artists.items[], coverArt.sources[], playability, isAlbumPreRelease, preReleaseEndDateTime` |
| `Playlist` | `uri, name, description (pode conter <a href=spotify:...> HTML), format, images.items[0].sources[], ownerV2.data.{uri,name,username,avatar}, attributes[{key,value}]` |
| `Episode` | `uri, name, description, duration, releaseDate.isoString, coverArt, podcastV2.data.{uri,name,publisher.name}, playedState, restrictions.paywallContent` |

Imagens: `sources[] {url, width, height}`; capas de álbum vêm como 300/64/640, artistas
640/160/320 (ordem não ordenada, escolha por tamanho). Cores: `extractedColors.colorDark.{hex,isFallback}`;
`visualIdentity.squareCoverImage.extractedColorSet` contém paletas RGBA mais ricas (ignorável).

```json
{"data":{"searchV2":{"query":"{q}","tracksV2":{
  "items":[{"matchedFields":[],"item":{"__typename":"TrackResponseWrapper","data":{
    "__typename":"Track","uri":"spotify:track:{id}","id":"{id}","name":"Song",
    "duration":{"totalMilliseconds":204664},"contentRating":{"label":"EXPLICIT"},
    "playability":{"playable":true,"reason":"PLAYABLE"},"trackMediaType":"AUDIO",
    "artists":{"items":[{"uri":"spotify:artist:{id}","profile":{"name":"Artist"}}]},
    "albumOfTrack":{"uri":"spotify:album:{id}","id":"{id}","name":"Album",
      "coverArt":{"extractedColors":{"colorDark":{"hex":"#305868","isFallback":false}},
        "sources":[{"url":"https://i.scdn.co/image/{hex}","width":300,"height":300}]}}}}}],
  "pagingInfo":{"limit":20,"nextOffset":20},"totalCount":429}}}}
```

## Navegação (`browseAll`)

Variáveis: `pagePagination:{offset:0,limit:10}, sectionPagination:{offset:0,limit:99},
browseEndUserIntegration:"INTEGRATION_WEB_PLAYER"`.

Raiz `data.browseStart` (`BrowseSectionContainer`, `uri spotify:page:{id}`):
`sections.items[] {uri: spotify:section:{id}, data:{__typename, title.transformedLabel}, sectionItems.items[]}`.
Nenhuma informação de paginação foi retornada.

- Seção `BrowseUnsupportedSectionData` tem um slot de exibição de anúncio (`spotify:promotion:...`, conteúdo `UnknownType`): pule.
- Seção `BrowseGridSectionData` "Browse all": 70 cards.
  - `content.__typename = BrowseSectionContainerWrapper` (uri `spotify:page:{id}`, uma página de categoria/gênero):
    `content.data.data.cardRepresentation.{title.transformedLabel, backgroundColor.hex, artwork.sources[]}`.
  - `content.__typename = BrowseXlinkResponseWrapper` (uri `spotify:xlink:{id}`, data `BrowseClientFeature`, ex. eventos ao vivo):
    `content.data.{title.transformedLabel, backgroundColor.hex, artwork.sources[], iconOverlay.sources[], featureUri}`.

```json
{"uri":"spotify:page:{id}","content":{"__typename":"BrowseSectionContainerWrapper",
 "data":{"__typename":"BrowseSectionContainer","data":{"cardRepresentation":{
  "title":{"transformedLabel":"Music"},"backgroundColor":{"hex":"#dc148c"},
  "artwork":{"sources":[{"url":"https://i.scdn.co/image/{hex}","width":300,"height":300}]}}}}}}
```

## Home

### `home`

Variáveis: `timeZone:String` (IANA, ex. `Europe/Zurich`), `sp_t:String` (valor do
cookie `sp_t` definido em `.spotify.com` pela primeira página accounts/open; um id por navegador,
placeholder `{sp_t}`), `facet:""` (filtro de chip, vazio = tudo), `sectionItemsLimit:10`,
`homeEndUserIntegration:"INTEGRATION_WEB_PLAYER"`, `includeHomeChapterVideoCards:false`,
`includeEpisodeContentRatingsV2:true`.

Raiz `data.home` (`HomeResponsePayload`):
- `greeting.transformedLabel` ("Boa noite").
- `homeChips[] {id, label.transformedLabel, subChips[{id,label}]}`: ids `music-chip`,
  `podcasts-chip`, `audiobooks-chip`; sub `*-following-chip`. Presumivelmente passado como `facet`.
- `sectionContainer.sections {totalCount, items[]}` (31 seções). Cada seção:
  `{uri: spotify:section:{id}, data:{__typename, title.transformedLabel, subtitle?, headerEntity?, iconName?},
  sectionItems:{items[], totalCount, pagingInfo.nextOffset}}`; cada item:
  `{uri, data (HomeRecsItemData|null), content: wrapper}`.

| Seção `data.__typename` | Títulos vistos | Itens |
|---|---|---|
| `HomeShortsSectionData` | (nenhum; grid rápido superior) | 1 `Playlist` |
| `HomeGenericSectionData` | "Recomendações de rádios", "Seus mixes favoritos", "Seus artistas favoritos", "Para fãs de {artist}", novos audiolivros | até 10 de `Playlist`, `Artist`, `Audiobook` (`PodcastOrAudiobookResponseWrapper`); `totalCount` maior, `nextOffset` 10 |
| `HomeRecentlyPlayedSectionData` | "Recents" | 1 `ListResponseWrapper/List` uri `spotify:list:recents:main` |
| `HomeFeedBaselineSectionData` | "Feito para você", "Com base em escutas recentes", "Vídeos que você pode gostar" | 1 `Playlist` ou `EpisodeOrChapterResponseWrapper/Episode` (cards de feed vertical) |

`headerEntity` é `ArtistResponseWrapper` (avatar + nome do artista antes do título) ou `UnknownType`.
Cards de playlist: `name, description, format, images.items[0].sources[0].url, ownerV2.data.name`.
Cards de artista: `profile.name, visuals.avatarImage`. Audiolivro: `name, authorsV2[].name, coverArt`.

Lista de Recents: `content.data.items.items[] {uid, entity, formatListAttributes[{key,value}]}`;
`entity` = `EntityResponseWrapper {_uri, data:{entityTypeTrait.type (ENTITY_TYPE_ARTIST|ENTITY_TYPE_PLAYLIST...),
identityTrait.{name,type,description,contributors}, visualIdentityTrait.squareCoverImage, typedEntity (wrapper ou UnknownType), sharingInfo}}`.
`spotify:collection:tracks` (Curtidas) aparece aqui; seu atributo `group_metadata`
(base64 protobuf) nomeia um uri de playlist filha, que então é buscado com `lookupChildEntities`.

```json
{"data":{"home":{"__typename":"HomeResponsePayload","greeting":{"transformedLabel":"Boa noite"},
 "homeChips":[{"id":"music-chip","label":{"transformedLabel":"Música"},"subChips":[{"id":"music-following-chip"}]}],
 "sectionContainer":{"uri":"spotify:page:{id}","sections":{"totalCount":31,"items":[
  {"uri":"spotify:section:{id}",
   "data":{"__typename":"HomeGenericSectionData","title":{"transformedLabel":"Rádios recomendadas"},
          "subtitle":{"transformedLabel":"..."},"headerEntity":{"__typename":"UnknownType"}},
   "sectionItems":{"totalCount":15,"pagingInfo":{"nextOffset":10},"items":[
    {"uri":"spotify:playlist:{id}","data":{"__typename":"HomeRecsItemData"},
     "content":{"__typename":"PlaylistResponseWrapper","data":{"__typename":"Playlist",
       "uri":"spotify:playlist:{id}","name":"Radio X","format":"inspiredby-mix",
       "images":{"items":[{"sources":[{"url":"https://pickasso.spotifycdn.com/image/{...}"}]}]},
       "ownerV2":{"data":{"__typename":"User","name":"Spotify","uri":"spotify:user:spotify"}}}}}]}}]}}}}}}
```

### `feedBaselineLookup`

Variáveis `uris:[String]`: os content uris de todos os itens `HomeFeedBaselineSectionData` (19, playlists e episódios).
Resposta `data.lookup[]` wrappers (`_uri` + `data`) com mídia de prévia para o feed vertical:
- Playlist: `data.previewItems.items[]` = `TrackResponseWrapper` com `name, uri, albumOfTrack.coverArt,
  previews.audioPreviews.items[].url` (mp3 de prévia em `p.scdn.co`), `canvas.{url,type,thumbnail}` (pode ser null).
- Episódio: `data.previewPlayback.{audioPreview.cdnUrl, videoPreview.{fileId, offset, placeholderImage}}`.
Opcional para um cliente que não mostra prévias.

### `lookupChildEntities`

Variáveis `uris:["spotify:playlist:{id}"]`. Resposta `data.lookupEntities[] {__typename:"Entity", uri,
visualIdentityTrait.squareCoverImage.{image.data.sources[].url, originalInstances[].flatFile.cdnUrl}}`
(arte de Curtidas `misc.scdn.co/liked-songs/liked-songs-{300|640}.png`).

## Decoração do player na inicialização

Disparado em todo carregamento do app (também após navegar para `/search` com recarregamento) uma vez que o
estado do player restaurado é conhecido (do cluster Connect, veja CAPTURE-MAP).

| Chamada | Variáveis | Resposta |
|---|---|---|
| `decorateContextTracks` | `uris:[50 x spotify:track]` (fila do contexto atual) | `data.tracks[] {__typename:"Track", uri, name, duration.totalMilliseconds, contentRating.label, artists.items[].{uri,profile.name}, albumOfTrack.{uri,name,coverArt.sources[]}, associationsV3}` |
| `fetchEntitiesForRecentlyPlayed` | `uris:[1 context uri]` (playlist atual/última) | `data.lookup[]` wrapper: `Playlist {uri,name,description,format,images,ownerV2.data.{name,uri,username}, attributes[]}` |
| `pathfinder/v1/query` | `uris:[3..5 spotify:track]` (faixa atual e vizinhos) | `data.lookupEntities[]` (veja abaixo) |

`pathfinder/v1/query` é uma persisted query sem nome (sem `operationName` no corpo)
enviada por um módulo cliente diferente: cabeçalhos não têm `app-platform` e `spotify-app-version`,
`accept: */*`, `content-type: application/json`; ainda `authorization` + `client-token`.
Corpo `{"variables":{"uris":[...]},"extensions":{"persistedQuery":{"version":1,"sha256Hash":"f952da03..."}}}`.
É um lookup genérico de entidade (provavelmente metadados de agora-tocando / media-session):

```json
{"data":{"lookupEntities":[{"__typename":"Entity","uri":"spotify:track:{id}",
 "entityTypeTrait":{"type":"ENTITY_TYPE_TRACK"},
 "consumptionExperienceTrait":{"duration":{"seconds":215,"nanoSeconds":0}},
 "identityTrait":{"name":"Song","contentHierarchyParent":{"uri":"spotify:album:{id}"}},
 "typedEntity":{"data":{"__typename":"Track","uri":"spotify:track:{id}","name":"Song",
   "albumOfTrack":{"uri":"spotify:album:{id}","name":"Album","coverArt":{"sources":[...]}},
   "artists":{"items":[{"uri":"spotify:artist:{id}","profile":{"name":"Artist"}}]},
   "duration":{"totalMilliseconds":215813},"playability":{"playable":true},
   "relinkingInformation":null}}]}}}
```

## Gaps

| Desconhecido | Captura que responderia |
|---|---|
| Ops de busca para podcasts, episódios, usuários, gêneros, audiolivros (nomes, hashes) | 03-search: clique nesses chips em `/search/{q}` |
| Op de página de gênero/categoria (`spotify:page:{id}` dos cards de navegação) e seu layout de seção | abra um card de navegação (ex. Pop) |
| Seção "Mostrar tudo" / paging da home (`nextOffset` use) | clique no título de uma seção da home |
| `home` com `facet` definido (formato de valor do chip) | clique no chip Música / Podcasts na home |
| Segunda página de uma aba de busca (`nextOffset` null no final?) | role uma aba até o final |
| Adicionar/remover/limpar pesquisas recentes (mutações) | clique em um resultado, depois remova/limpe recentes |
| `searchSuggestions` com `offset>0`; `isPrefix` não-null | gatilho desconhecido; digite e role o dropdown |
| Formatos de erro (token expirado, hash desconhecido -> `PersistedQueryNotFound`?) | reemita com token antigo / hash ruim |
| Fonte exata de `fetchEntitiesForRecentlyPlayed` / uris v1 (cluster vs armazenamento local) | captura de boot com frames do dealer correlacionados |
