# Mapa de captura do Web Player

Notas de sala limpa para a nova API, tiradas de capturas de rede do open.spotify.com
(conta gratuita, Chrome 155 com Widevine, 2026-10-15). Capturas brutas guardam tokens e
ficam fora do repositório. `{user}` é o id da conta, `{id}` um id base62, `{hex}` um id
de arquivo ou dispositivo. Chamadas GraphQL vão todas para `POST api-partner.spotify.com/pathfinder/v2/query`
e são listadas por `operationName`.

## Sessão

| Etapa | Chamadas |
|---|---|
| Login (própria página do Spotify) | `accounts.spotify.com/login`, `login/code/send`, `login/code/verify` (fluxo de código por e-mail) |
| Web token | `GET open.spotify.com/api/token` |
| Client token | `POST clienttoken.spotify.com/v1/clienttoken` |
| Pontos de acesso | `GET apresolve.spotify.com/` (spclient + dealer hosts) |
| Canal push | WebSocket `{region}-dealer.g2.spotify.com` |
| Config | `POST {spclient}/remote-config-resolver/v3/configuration`, gql `accountAttributes`, `profileAttributes` |

## Reprodução (dispositivo Web Player)

| Ação | Chamadas |
|---|---|
| Registrar dispositivo | `POST {spclient}/track-playback/v1/devices`, `PUT connect-state/v1/devices/hobs_{hex}` |
| Tocar um contexto | `POST connect-state/v1/player/command/from/{hex}/to/{hex}` |
| Resolver áudio | `GET manifests/v9/json/sources/{hex}/options/supports_drm`, `GET storage-resolve/v2/files/audio/interactive/10/{hex}`, `GET seektables.spotifycdn.com/v1/seektable/{hex}` |
| DRM | `GET widevine-license/v1/application-certificate`, `POST widevine-license/v1/audio/license` (EME, tratado pelo CDM do navegador) |
| Áudio | `GET audio-fa.scdn.co/audio/{hex}` (segmentos criptografados) |
| Reportar estado | `PUT track-playback/v1/devices/{hex}/state` (em play, pause, seek, skip) |
| Volume | `PUT track-playback/v1/devices/{hex}/volume` |
| Mídia da próxima faixa | `GET track-playback/v1/media/{uri}` |
| Estado do cluster | `GET connect-state/v1/cluster`, depois pushes do dealer |
| Eventos | `POST gabo-receiver-service/v3/events` (eventos de reprodução e UI) |

Conta gratuita: sem botão de embaralhar em um contexto de playlist na UI web.

## Catálogo e busca

| Ação | Operações GraphQL |
|---|---|
| Home | `home`, `feedBaselineLookup` |
| Busca | `recentSearches`, `searchSuggestions` (conforme digita), `searchTopResultsList` (enviar), `searchTracks`, `searchArtists`, `searchAlbums`, `searchPlaylists` |
| Navegação | `browseAll` |
| Faixa | `getTrack`, `internalLinkRecommenderTrack`, `similarAlbumsBasedOnThisTrack`, `canvas`, `npvV2TrackSupplementalSections` |
| Álbum | `getAlbum` |
| Artista | `queryArtistOverview`, mais `GET playlist/v2/list/popular-release-segments-main-roles/artist_{id}` |
| Letras | `GET color-lyrics/v2/track/{id}/image/{cover}`, `GET timesynced-text-distribution/v0/get-timesynced-text/{uri}` |
| Metadados extras | `POST extended-metadata/v0/extended-metadata`, `GET metadata/4/track/{hex}` |

## Biblioteca e playlists

| Ação | Chamadas |
|---|---|
| Lista da biblioteca | gql `libraryV3`, `GET playlist/v2/user/{user}/rootlist` |
| Página Curtidas | gql `fetchPlaylist` em `collection/tracks` |
| Página da playlist | gql `fetchPlaylist`, `fetchPlaylistContents`, `fetchPlaylistMetadata`, `playlistPermissions`, `isCurated` |
| Seguir/deixar de seguir playlist | `POST playlist/v2/user/{user}/rootlist/changes` |
| Criar playlist | `POST playlist/v2/playlist`, depois `rootlist/changes` |
| Renomear | `POST playlist/v2/playlist/{id}/changes` |
| Adicionar faixa à playlist | gql `addToPlaylist` |
| Faixas sugeridas | `POST playlistextender/extendp/` |
| Está na biblioteca? | gql `areEntitiesInLibrary`, `POST collection/v2/contains` |

## Curtidas e seguidores

| Ação | Operações GraphQL |
|---|---|
| Curtir faixa | `addToLibrary` |
| Descurtir faixa | `applyCurations` (com `editablePlaylists`) |
| Salvar/desalvar álbum | `addToLibrary` / `removeFromLibrary` |
| Seguir/deixar de seguir artista | `addToLibrary` / `removeFromLibrary` |

## Ainda não capturado

Remover faixa de playlist, deletar playlist, reordenar, Connect para outro dispositivo,
podcasts, anúncios (`ads/v2/config` é buscado na inicialização).
