# Anúncios em conta gratuita (Web Player)

Observado em 2026-10-15 com uma conta gratuita no Chrome desktop, tocando uma playlist por
cerca de 16 minutos. Um intervalo de anúncio de áudio aconteceu. Capturas brutas ficam fora do repositório.

## De onde vêm os anúncios

| Tipo | Fonte |
|---|---|
| Anúncio de áudio entre faixas | Inserido pelo servidor na máquina de estado de reprodução |
| Banner ("leaderboard") | Gerenciador de anúncios da página `leaderboard`, carregado em um iframe |
| Anúncios na home, carrosséis | Gerenciadores de anúncios da página `home` e amigos |
| Pré/mid/pós-rolls de podcast | Slots na configuração de anúncios, não observados ainda |

A página agrupa seus gerenciadores em `Platform.getAdManagers()`: `config`, `audio`,
`inStreamApi`, `leaderboard`, `home`, `vto`, `survey`, `adOpportunityLogger`. O registro
também tem uma `ForceVolumeAPI`.

## Cronograma do anúncio de áudio

1. Na inicialização a página busca `GET spclient.wg.spotify.com/ads/v2/config`. O slot
   `stream` carrega `stream_time_elapsed_ms: 870000` (14,5 min) e `linear: true`.
2. Quando tempo de escuta suficiente passou, o servidor adiciona o anúncio à resposta de
   `PUT track-playback/v1/devices/{device}/state`. Ele chega uma faixa à frente: estava na
   resposta no início da faixa anterior, cerca de 4 minutos antes de tocar.
3. O anúncio fica em `state_machine.tracks[]` como uma faixa, com `content_type: "AD"`,
   `metadata.uri: "spotify:ad:{id}"`, um nome, um anunciante, uma duração (27 s aqui) e
   `manifest.file_urls_mp3`. O estado correspondente tem `disallow_seeking: true` e
   transições de pular vazias (`disallow_seeking_reasons: ["ad_disallow"]`).
4. O áudio é um MP3 simples de `adstudio-assets.scdn.co/mp3/{hash}.mp3`. Ele não
   usa Widevine.
5. Durante o anúncio, a página o reporta como uma faixa: `melody/v1/msg/batch`
   (`jssdk_playback_start` com `play_track: spotify:ad:…`), eventos `gabo-receiver-service`,
   `aet.spotify.com/v2/t`, e as usuais atualizações de estado `track-playback`. O
   dealer WebSocket envia o estado do cluster com o anúncio como faixa atual.
6. Após o anúncio terminar, a próxima faixa da playlist começa normalmente.

`PlayerAPI.getState().item` durante o anúncio: `type: "ad"`, `uri: "spotify:ad:…"`, `title`
(sem `name`), `advertiser`, `url` (click-through), `duration`, `images`. Restrições:
`canSkipNext`, `canSkipPrevious` e `canSeek` são false; `canPause` é true.

## O que a ponte faz agora

A ponte reporta anúncios como itens com `kind: "ad"`, mais `name`, `advertiser`, duração
e restrições. A UI pode mostrá-los: quem é o anunciante, uma contagem regressiva, e nenhum botão
pular.

## Opções para o engine

| Opção | O que acontece | Risco da conta | Notas |
|---|---|---|---|
| Mostrar anúncios | O anúncio toca e a UI mostra | Nenhum | Corresponde aos termos do Spotify |
| Diminuir o volume durante anúncios | O anúncio toca e é reportado, o usuário ouve pouco | Baixo a médio | `ForceVolumeAPI` existe. A página pode empurrar o volume de volta ou pausar um anúncio em mudo. Precisa de teste. |
| Bloquear o anúncio na rede | O item do anúncio ou seu MP3 nunca toca | Maior | Muda o que a página reporta. É o tipo de adulteração que o Spotify pode detectar, e quebra se eles mudarem o formato. |

Bloquear anúncios vai contra os Termos de Uso do Spotify (veja o aviso legal do README). Qualquer
que seja a escolha, deve viver em seu próprio módulo atrás da interface `:api`, para que a política
possa mudar sem tocar a UI ou a ponte do player.

## Testado: bloqueando o áudio do anúncio (2026-10-16)

Configuração: conta gratuita, Chrome desktop, requisições para `adstudio-assets.scdn.co` abortadas
(`blockedbyclient`) desde o início de uma sessão longa de escuta.

1. Às 00:20:13 o servidor colocou um anúncio (`spotify:ad:…`, 27 s) próximo na máquina de estado, como antes.
2. O player tentou carregar seu MP3; a requisição foi abortada.
3. A página reportou um erro de reprodução para o anúncio em si: `melody` `jssdk_playback_error`
   (`content_type: "ad"`), então `PUT track-playback/.../state` com `debug_source: "error"`.
4. O servidor passou adiante: o próximo estado de máquina não tinha mais o anúncio, e a próxima faixa da playlist
   começou em menos de um segundo (`jssdk_playback_start`, `started_playing`, então
   `played_threshold_reached` 30 s depois, como qualquer faixa).
5. O anúncio esteve na tela por menos de 0,5 s. Nenhum comando de pular foi necessário.

Então o engine bloqueia anúncios recusando o host de áudio do anúncio, e deixa a página reportar o que
realmente aconteceu (uma falha no carregamento do anúncio). Ele nunca reporta um anúncio como tocado.

Regras do engine:
- Android WebView: em `shouldInterceptRequest`, responda requisições para `adstudio-assets.scdn.co` com uma
  resposta de erro vazia. Host desktop CDP: `Fetch.failRequest` (`BlockedByClient`) para o mesmo padrão.
- Fallback, se um item de anúncio ficar atual por mais de 1 s (novo host de anúncio, anúncio de vídeo): toque a próxima
  faixa do contexto com `player.play {contextUri, trackUri}`, e adicione o novo host à lista de bloqueio.
- Banners, anúncios da home e carrosséis nunca chegam ao usuário: a página do Web Player fica escondida e a UI é
  nativa. Suas requisições ainda podem ser bloqueadas para economizar dados.
- A UI dropa itens `kind: "ad"` do que mostra.

## Ainda para capturar

- Se anúncios bloqueados voltam mais cedo (ritmo após um anúncio falhado), em uma sessão de 1 hora.
- Anúncios de vídeo e slots de anúncio de podcast, que podem usar outros hosts.

## Medido novamente (2026-10-20): um anúncio recusado não avança mais

Configuração: conta gratuita, Chrome desktop, a ponte atual do app, cerca de duas horas de escuta.

- O travão do gerenciador de anúncios da página (`ads.off`) não para anúncios de áudio: um anúncio após ~17 min com o travão
  ligado, um após ~19 min sem ele. O servidor os enfileira independentemente do que os gerenciadores da página dizem.
- Com o MP3 do anúncio recusado (abortado, ou respondido `403` como no Android), o anúncio agora fica atual para
  sempre: buffer em 0:00, 20 min em uma execução. `inStreamApi.skipToNext()`, `player.next` e
  `player.play` na próxima música são todos recusados (`ad_disallow`). O comportamento de 2026-10-16 (erro,
  próxima música em um segundo) acabou.
- Um recarregamento de página deixa ele para trás: a nova página registra um novo dispositivo, que o anúncio não segue, e
  tocar o contexto novamente começa uma música em um segundo. Este é o último recurso do app.
- O "Test ad" (`spotify:ad:test`) visto nos logs do dispositivo era o próprio fake do `tools/playback_test.py`.

## Onde o anúncio fica na máquina de estado

O anúncio chega em uma resposta `PUT track-playback/v1/devices/{device}/state` quando a música antes dele
começa (cerca de 3 min antes). O estado da música tem estas transições:

| transição | primeira tentativa | após um anúncio foi deixado de lado |
|---|---|---|
| `advance` (fim natural) | o estado do anúncio | o estado do anúncio |
| `skip_next` | a próxima música | o estado do anúncio |
| `show_next` (mostrado como próximo) | a próxima música | a próxima música |

O estado do anúncio não tem transições e restrições `ad_disallow`.

## O que o engine faz agora

Três etapas, medidas no desktop (conta gratuita, 20/10, ~1 h 30 de escuta, 5 tentativas de anúncio):

1. `AdBlock.withoutAds` reescreve toda resposta que leva a um anúncio antes que a página a veja: o
   `advance` de uma música que leva a um anúncio vai para onde seu `skip_next` vai. A próxima música começou
   60 ms após o fim da música, e todo estado que a página reportou foi respondido `200`.
   `show_next` não é um alvo usável: aponta para um estado apenas de exibição, e uma página enviada para lá
   teve todos os seus relatórios recusados (`502 unknown_state_ref`) e parou de tocar.
2. Quando `skip_next` leva ao anúncio também (a próxima tentativa do Spotify, após um anúncio foi deixado de lado uma vez),
   `AdBlock.adAhead` nomeia a música que a página mostra como próxima, e o player a toca 1 s antes
   da música acabar (`player.play` no mesmo contexto). Uma nova reprodução coloca o anúncio devido primeiro, mas um anúncio
   no início de uma reprodução, seu áudio recusado, avança sozinho: a próxima música veio em cerca de um
   segundo (três tentativas, no final e no meio de uma música), todos os relatórios foram `200`, as músicas
   após ela seguiram sem anúncio, e músicas que o usuário havia colocado na fila permaneceram na fila. Um "próximo" enquanto um anúncio
   está devido é enviado da mesma forma, já que o Spotify aponta o pular para o anúncio também e uma página que pisa
   em um anúncio fica nele. A UI mostra um anúncio apenas após 2 s, então esse nunca aparece.
   Visto no telefone de um testador: enquanto o Spotify recusa licenças (429, após pular rápido), a música
   após o anúncio não consegue iniciar e o anúncio fica até o limite passar.
3. O host de áudio do anúncio permanece recusado, e o recarregamento permanece como o último recurso para um anúncio que já
   é o estado atual.

No Android as respostas passam por `ControlRelay`, que aplica as etapas 1 e 2. O host desktop CDP
não reescreve respostas ainda.

Na sessão medida: nenhum anúncio ouvido ou mostrado, nenhum recarregamento.
