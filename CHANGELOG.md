# Changelog

Cada release no GitHub mostra sua seção abaixo. O app mostra também quando oferece a atualização. Todo detalhe: [docs/CHANGELOG-full.md](docs/CHANGELOG-full.md).

## 2.2.0

- 📺 **Chromecast** para extensão, músicas baixadas e do dispositivo, sem Google Play Services: dispositivos Cast aparecem no seletor de dispositivo e a música é streamada do seu telefone pela sua rede.
- 📱 **Tablets, dobráveis e Samsung DeX**: uma barra de navegação no Classic, uma barra lado a lado no Immersive, mais colunas de grade, players de dois painéis, rotação livre, janelas redimensionáveis e atalhos de teclado.
- 🚗 **Android Auto**: abas Home, Biblioteca e Downloads com capa, busca, Iniciar rádio, e músicas baixadas que tocam offline.
- 🎬 **Canvas no player Immersive**: o clipe da música toca atrás do player e a capa se abre dentro dele.
- 🎙️ **Podcasts**: não tocados primeiro ou ocultar tocados, marcas New em episódios novos, ticks de tocados e barras de progresso, e novos lançamentos de shows seguidos abrem o show.
- ➕ **Fila de listas inteiras**: Tocar depois e Adicionar à fila para playlists, álbuns e Curtidas. Com uma fonte de extensão, sua fila segue no Spotify.
- 🎤 **Letras**: toque em uma palavra para tocar daquela palavra, letras de outro catálogo não deslizam mais, e notas da Genius no Immersive.
- 🔁 **Repetir uma vez**, e repetir uma reinicia em 0:00 em músicas de extensão.
- 🔊 **Som mais suave**: sem clique em pulas e pausas, e um crossfade que realmente desvanece, músicas de extensão, baixadas e do dispositivo incluídas.
- 💾 **Downloads pertencem à conta**: sair coloca eles de lado, entrar de volta traz eles de volta.
- 🛡️ **Extensões mais seguras**: seus streams e downloads não podem alcançar sua rede local, cada redirecionamento é verificado, o Connect funciona com elas abaixo do Android 13, e seus erros são mais claros.
- 🛠️ **Correções**: o coração de curtida fica certo após uma reinicialização e após uma curtida feita no app oficial; a linha de fonte de áudio não empurra mais o título para cima; embaralhar e repetir sempre ao alcance no player Immersive, com um layout paisagem; links clicáveis em descrições; contagem de músicas ao lado da duração de uma playlist; lançamentos do artista em ordem completa de data; arquivos locais do Spotify em playlists; e os bugs reportados no Discord.
- 🌍 Traduções de tudo novo.

## 2.1.0

O primeiro release estável desde 2.0.1, com tudo dos betas 2.0.2 (suas notas estão abaixo) e mais.

- ✨ **Novo visual Immersive** (opcional, estilo Apple Music): barras Liquid Glass que se dobram quando você rola, player e letras animados, folhas de vidro, e páginas próprias para álbuns, pastas, playlists locais, extensões e episódios de podcast. Mude em Configurações, Exibição; Classic continua o padrão. Cada visual mantém sua própria **cor de destaque** e mostra apenas suas próprias configurações.
- 🧩 **Extensões**: fontes de áudio e download são extensões agora, ordenadas como quiser com o Spotify entre elas, encontradas no GitHub, GitLab e Codeberg. Mais seguras: apenas hosts públicos, novas permissões precisam do seu OK, uma falha não derruba mais o app.
- 📱 **Spotify Connect com fontes de extensão**: quando uma extensão toca uma música do Spotify, o telefone permanece um dispositivo Connect. Os apps oficiais do Spotify e seus outros dispositivos veem e controlam, e a música conta no Spotify através de uma reprodução real em mudo em baixa qualidade. Desative em Configurações se preferir economizar dados e bateria.
- 📊 **Reproduções contam no Spotify**, Recents incluídos. **Estatísticas de audição** e **atividade de amigos**.
- 👥 **Playlists colaborativas**: saia de uma, ou gerencie como o proprietário.
- 🚫 **Menos "Spotify recusou tocar isso"** (402) e nenhuma música pulando sozinha; melhor bloqueio de anúncios (mais hosts de anúncios, anúncios encontrados por seus metadados, recarregamentos que ficam silenciosos).
- 🔀 **Embaralhar no Immersive** passa por embaralhar, smart shuffle e desligado, como no Classic.
- 🔎 **Uma música tocada de busca** continua com músicas relacionadas, não mais com suas Curtidas.
- 📻 **Rádios**: o cabeçalho de tocando-de e o título da música abrem a página e álbum certos.
- 👋 **Novo onboarding**: como o NEXTFY funciona, depois o que são extensões e onde encontrá-las (mostrado uma vez após a atualização também).
- 👆 **Toques mais fáceis**: botões de voltar e perfil mais largos, e as barras de vidro não deixam mais toques atravessarem para a página.
- 🎧 **Fones e carro**: pulos de 15 s para podcasts, Android Auto que continua tocando, reprodução em segundo plano que continua rodando.
- ⚡ **Mais rápido e leve**: páginas e Curtidas mantidas no telefone, letras e buscas em cache, um cache de imagem menor, listas e animações mais suaves, Remover animações respeitado em todos os lugares.
- 🌍 **Mais idiomas**: chinês, russo, polonês, turco, indonésio, e espanhol para Argentina e México.

## 2.0.2-beta.8

- 🎚️ **Ordem da fonte** (Configurações, Reprodução e Downloads): coloque suas fontes de áudio e download na ordem que quiser; cada música é perguntada de cima para baixo. O Spotify tem seu próprio lugar também, primeiro se quiser. Cada fonte mostra apenas o que importa, detalhes em um toque. Instalar uma segunda fonte não entra em conflito mais com a primeira.
- 🔄 **Atualizações**: um botão Verificar agora em Configurações, Sobre, e o app verifica mais vezes (no máximo uma vez por hora, novamente 10 min após uma verificação falhada).
- 💎 **Liquid Glass** na barra superior de todas as abas, com fundo arredondado; o desfoque segue as mudanças de aba ao vivo. Corrige voltar de uma playlist que ficava na playlist.
- ↕️ **Fila do Immersive**: uma música solta vai direto para seu lugar, sem pular de volta primeiro.
- 🎙️ **Podcasts**: os botões de próximo e anterior do fone pulam 15 s para frente e trás (Configurações, Reprodução).
- 📖 **Significados de músicas da Genius** no visual Immersive.
- 🔒 **Extensões mais seguras**: chamadas de rede limitadas a hosts públicos, uma atualização que pede mais precisa do seu OK, gravações do Spotify concedidas uma por uma, uma falha de extensão não derruba mais o app.
- 🌍 Mais textos traduzidos.

## 2.0.2-beta.7

- 💎 **Liquid Glass real** (Immersive): refração de lente, aro especular e um desfoque ao vivo do que rola atrás, agora também em telefones com desfoque de janela do sistema desligado (Android 13+; desfoque ao vivo no Android 12). Molas e feedback de pressionar mais suaves no visual.
- ⏭️ **Músicas pulando sozinhas**: quando o Spotify derruba várias músicas em sequência, o app para e avisa em vez de encadear pela sua fila. Relatórios de diagnóstico agora dizem por que cada música mudou.
- ↕️ **Fila do Immersive**: músicas arrastadas ficam onde você solta, se movem entre "Próximo na fila" e "Próximo da", e a lista rola nas bordas.
- 🖼️ **Capas nítidas no Immersive** após abrir as letras.
- 🎨 **Cor de destaque** agora se aplica ao visual Immersive; sem mais som de clique no player em tela cheia dele; letras seguem a música após um pular.
- ⏮️ **Anterior** volta uma música em vez de reiniciar quando tocado logo após um pular.
- 🔁 **Reabrir o app** traz de volta a música de uma extensão ou download, sua fila e posição.
- ✖️ **Botão Limpar** em campos de busca.

## 2.0.2-beta.6

- 🔔 **Popup de atualização corrigido**: ele aparece assim que o app sabe da atualização, um toque acidental não fecha mais, e após "Depois" a atualização fica em Configurações, Sobre.
- 🗂️ **Configurações em abas** (visual clássico): Reprodução, Downloads, Aparência, Extensões, Sobre. Sobre mostra sua versão e a atualização quando há uma.
- 🧩 **Extensões**: Atualizar realmente atualiza agora (sem cache obsoleto, erros reais, verificação de atualização de extensões instaladas). Extensões também são encontradas e instaladas de **GitLab** e **Codeberg** (tópico `NEXTFY-extension`). Desinstale uma extensão direto da lista (ícone de lixeira), que agora também limpa seu login e cache.
- 🚗 **Android Auto**: reprodução iniciada do carro não para mais após alguns segundos, e os botões do carro continuam funcionando.
- 🔀 **Crossfade real** para músicas tocadas de extensões, downloads e arquivos do dispositivo (Configurações, Reprodução). Streams do Spotify mantêm seu desfoque de saída e entrada.
- 🖼️ **Capas nítidas em todos os lugares**: notificação, tela de bloqueio, Android Auto, cartão de compartilhamento. Corrige uma falha no início da reprodução (#77).
- ✨ **Visual Immersive**: arraste músicas para reordenar a fila, e a linha "tocando de" da fonte não está mais cortada.

## 2.0.2-beta.5

- ✨ **Novo visual Immersive** (opcional, estilo Apple Music): barra de abas de vidro e player mini que se dobram quando você rola, player e letras animados, Configurações agrupadas, folhas de vidro (Spotify Connect, fila, qualidade…). Oferecido na primeira abertura após a atualização; mude a qualquer hora em Configurações, Exibição. Classic continua o padrão.
- 🎤 **Letras** (Immersive): a nova linha perde seu desfoque no momento em que se torna atual.
- 🖼️ **Capas nítidas** no player: a imagem grande em vez de um thumbnail esticado.
- 🎧 **Reprodução em segundo plano**: o player continua rodando quando você muda de app ou desliga a tela.
- 🚫 **"Spotify recusou tocar isso" (402)**: comandos enviados como os do navegador, uma tentativa de rede do Chromium após uma recusa, e sem mais cópia fantasma deste telefone entre seus dispositivos. Ainda recusado? Por favor envie um relatório de diagnóstico desta versão: ele agora abre com um resumo curto da causa.
- ⏭️ **Sem mais músicas pulando sozinhas** quando o Spotify limita licenças de áudio: o app espera em vez de perguntar de novo.
- 🧹 **Relatórios de diagnóstico mais leves**: um terço mais curto, sem as linhas repetidas.

## 2.0.2-beta.4

- 🧩 **Fontes de download são extensões agora**: o app não carrega mais nenhuma fonte de download. Instale uma extensão de fonte de download (Configurações, Extensões) e downloads funcionam como antes: verificados, criptografados, mantidos no NEXTFY. Sem uma, downloads avisam.
- 🔌 **Para autores de extensões**: o hook `download-source`, eventos de `playback` (início, progresso, pausa, retomar, buscar, pular, fim) e `spotify.raw.request` (docs/extensions/API.md).
- 📊 **Reproduções agora contam no Spotify**: uma vez que o dispositivo é registrado, os relatórios de reprodução (melody) saem do próprio app.
- 👥 **Playlists colaborativas, lado do proprietário**: remova alguém como colaborador ou da playlist, ou pare a colaboração (com um aviso). Links de convite permanecem válidos até expirarem.
- 💾 **Sem botão Salvar** nas suas próprias playlists.
- ➕ **Adicione uma playlist ou álbum a outra playlist**, com a escolha do que fazer sobre duplicatas.
- 🗂️ **Filtros de Biblioteca**: playlists, artistas, álbuns, músicas.
- 🌍 **Chinês, russo e polonês**.
- 📈 **Estatísticas de audição**: principais artistas e faixas (4 semanas, 6 meses, ano), minutos por semana contados no telefone, um distintivo "seu artista #N", e **atividade de amigos**.
- 🎬 **Vídeos musicais**: um interruptor para desligá-los, e **picture-in-picture** para clipes.
- 🎧 **Músicas tocadas por uma extensão de fonte de áudio**: o player mostra qual extensão toca e a qualidade que ela anuncia, a música aparece de imediato com sua capa grande, letras, Canvas e botão de curtir, e a fila funciona como com o Spotify (Tocar depois, Adicionar à fila, o rádio da música após a última música). Uma música que nenhuma extensão tem toca no Spotify em vez de ser pulada, e a próxima volta para a extensão.
- ⚡ **Inícios mais rápidos com extensões**: a próxima música é preparada durante a atual, e uma fonte lenta ou fora do ar é deixada mais cedo.
- 🩺 **Extensões dizem se sua fonte responde**: uma que não responde é mostrada inativa e pulada até responder de novo. As telas de extensões são mais simples.
- 📴 **Curtidas e adições feitas offline** são enviadas quando a rede volta.

## 2.0.2-beta.3

- 👋 **Sair de uma playlist colaborativa** que você entrou, do menu da playlist, como no web player.
- ⬇️ **Playlists baixadas tocam do seu telefone**: escolher uma música em uma lista totalmente baixada toca seu arquivo, online também, em vez de transmitir de novo.
- 🩺 **"Spotify recusou tocar isso"**: o relatório de diagnóstico agora diz muito mais sobre por que o Spotify recusa. Se acontecer com você, por favor envie um relatório desta versão.

## 2.0.2-beta.2

- 🎵 **Reprodução funciona de novo**: o beta.1 poderia deixar o player do Spotify sem um dispositivo ("Dispositivo não encontrado"), então nada tocava. A página agora começa exatamente como no 2.0.1: os relatórios de reprodução saem da página de novo, e a verificação de áudio protegido roda apenas após uma reprodução falhar.
- Tudo mais do beta.1 permanece (Recents, reproduções recusadas, anúncios, Connect, repetir, login, downloads, Android Auto, playlists, fonte).

## 2.0.2-beta.1

- 📊 **Suas reproduções contam no Spotify**: o relatório do que você realmente ouviu agora sai do app como a própria reprodução, então reproduções normais devem chegar ao seu Recents e ao seu Wrapped. Recents também não esconde mais uma reprodução cuja linha de playlist ou álbum não foi anunciada.
- 🎵 **"Spotify recusou tocar isso"**: uma reprodução que o Spotify reconhece mas nunca começa agora vai para os fallbacks em vez de ficar em 0:00.
- 📺 **Anúncios**: uma música deixada em pausa logo após um intervalo de anúncios começa sozinha.
- 💻 **Spotify Connect**: enquanto outro dispositivo toca, o NEXTFY não busca nem pula nele, então o computador para de pular faixas.
- 🔁 **Repetir** mantém sua escolha após uma nova reprodução ou recarregamento.
- 🔒 **Sem som nenhum?** Se o seu WebView não pode tocar áudio protegido, o app agora diz isso e pede para atualizar o Android System WebView e o Chrome.
- 🔑 **Login**: um telefone lento não é mais enviado de volta para a página de login em um loop.
- ⬇️ **Downloads**: versões lentas, remasterizadas e em destaque são encontradas.
- 🚗 **Android Auto** não congela mais em bibliotecas grandes (300 itens por pasta).
- ❤️ **O botão +** de outras músicas não é mais marcado porque uma playlist própria toca.
- 🖼️ **Playlists sem capa** mostram o mosaico de seus álbuns; playlists grandes mostram seus controles mais cedo.
- 🔤 **Nova fonte, Manrope**: texto cirílico e grego agora usa a fonte do app.

## 2.0.1

- 🎵 **"Spotify recusou tocar isso"**: nas contas onde o Spotify recusou toda reprodução (contas gratuitas em alguns telefones e países), um comando recusado é enviado novamente da forma como outros apps do web player enviam, e a reprodução continua desse jeito quando funciona. Se ainda recusar, por favor envie um novo relatório de diagnóstico: ele agora diz qual caminho foi tentado.

## 2.0.0

O NEXTFY 2.0 é um app novo, reconstruído do zero: Spotify sem anúncios e com downloads sem perdas, em uma conta gratuita.

- 🚫 **Sem anúncios, em conta gratuita**: os intervalos de anúncios do próprio player do Spotify são desligados na fonte. Escolha qualquer música, pule quanto quiser.
- ⬇️ **Downloads sem perdas**: músicas, álbuns, playlists e Curtidas em FLAC (CD ou 24-bit hi-res) ou pequeno AAC — a própria gravação, encontrada pelo ISRC no Tidal ou Qobuz, criptografada no seu telefone. Playlists baixadas permanecem sincronizadas.
- 🚀 **Um novo engine**: telas mais rápidas, pulos mais rápidos, e reprodução que se recupera sozinha.
- 🎤 **Letras palavra por palavra** de várias fontes, com tradução e **significados de músicas da Genius**.
- 🎬 **Vídeos musicais oficiais**, sincronizados, em tela cheia, silenciosos ou com o som do próprio clipe.
- 👥 **Playlists**: crie e edite, **adicione músicas** de uma busca, **playlists colaborativas** com convites e quem adicionou o quê.
- 🔀 **Smart Shuffle**, uma fila arrastável, crossfade, temporizador de sono.
- 🎙️ **Podcasts e audiolivros**, com seu próprio player e downloads.
- 📷 **Spotify Codes** para compartilhar, perfis, Recents, Browse, Novos lançamentos, Seu recap, Jam, Android Auto.
- 🌍 **7 idiomas**, atualizações no app, e um relatório de diagnóstico para enviar com um toque.

Esta instalação instala como **NEXTFY**, ao lado do NEXTFY Beta se você tiver.

## Desde 2.0.0-beta.9

- **Letras palavra por palavra de mais fontes** (tempo do Apple Music através do LyricsPlus, lrc.red, Kugou), alinhadas com as próprias do Spotify, palavras acesas inteiras conforme são cantadas; uma configuração escolhe palavra por palavra ou linha por linha.
- **Significados de músicas da Genius**: a lâmpada abre a história da música; linhas sublinhadas abrem sua explicação com um toque longo, mais gostadas primeiro, traduzidas para o seu idioma à medida que chegam.
- **Vídeos musicais com som próprio**: "Som: clipe" toca o áudio do clipe enquanto a música continua tocando silenciosamente no Spotify (ainda conta). Clipes em 1080p no Wi-Fi, menos congelamentos, o botão de tela cheia no canto do clipe, a capa certa após o clipe, e uploads de gravadora encontrados quando o Spotify mesmo tem o vídeo.
- **Adicione músicas a uma playlist** de uma busca do Spotify logo abaixo da capa, com sugestões.
- **Playlists colaborativas**: convide com um link, rostos dos membros no cabeçalho, quem adicionou cada música, e colaboradores podem adicionar e remover músicas.
- **Compartilhe como um Spotify Code**, com o link abaixo.
- **Fila**: uma linha mostra onde uma música arrastada vai cair, e uma música movida para dentro ou fora da fila não fica mais presa.
- **Smart Shuffle**: músicas sugeridas são marcadas e podem ser mantidas com +, e acendem quando tocam.
- **Links compartilhados abrem a página** (playlist, álbum, artista, show) em vez de iniciar.
- **Corrigido**: uma falha ao digitar ou excluir na busca de uma playlist enquanto música toca, um pular quando um clipe ia para tela cheia, a barra de status sobre a página de login, o + de uma música tocada de sua própria playlist, e letras difíceis de ler em algumas cores.
- **Uma falha ou congelamento agora aparece no próximo relatório de diagnóstico.**
- **Uma nova tela de boas-vindas** e uma configuração mais suave.
- **Player mais suave, mais leve na bateria**: a página oculta do player era redesenhada a cada quadro que o app desenhava (e continuava animando sua própria UI por baixo), e a barra de progresso redesenhava todo o player 15 vezes por segundo. A página agora fica fora do desenho do app e a barra redesenha apenas a si mesma, tão frequentemente quanto se move. Medido na tela do player: 0 quadros atrasados em vez de 86 %, cerca de um terço menos de CPU — e um toque em uma música ou "próximo" é ouvido ~150 ms mais cedo.
- **O player se recupera de uma página que falha ao carregar**: quando o Spotify respondeu um erro a um recarregamento (visto: 504), o app ficava nela, recusando todo comando até reiniciar. A página agora é carregada de novo, mais cedo primeiro, depois a cada minuto.
- **"Próximo" que não faz nada**: quando a página do player respondeu "ok" a um pular mas ficou na música (o som caiu, depois nada), a música seguinte agora é pedida pelo nome.
- **Spotify recusando reprodução (402)**: após uma recusa, o app agora também tenta levar a reprodução para este telefone primeiro, depois a lista no embaralhamento, antes de avisar; o relatório de diagnóstico agora diz que tipo de conta toca e se um comando recusado foi direcionado para este telefone ou outro dispositivo.
- **Smart shuffle do player mantém a música tocando**: em uma playlist longa a música não era encontrada e uma aleatória começava; a música tocando agora sempre fica primeiro.
- **Toques rápidos em "próximo" no player chegam onde devem, e a tela segue**: cada toque mostrava a mesma segunda música (segurada por segundos enquanto a página ia para a quinta), e um primeiro pular que o Spotify aplicou tarde poderia empurrar o player uma música além do alvo. A tela agora mostra a música que os toques somam, e onde os pulos aterrissaram é verificado e corrigido.
- **Uma música escolhida logo antes de um anúncio toca após o anúncio** em vez da fila antiga continuar (reportado com uma música escolhida em outra playlist).
- **Playlists de perfil sem uma capa própria mostram seu mosaico** em vez de um quadrado cinza.
- **"Reiniciar o player" em Configurações diz que fez.**
- **Embaralhar não volta mais**: um estado enviado pela página antes de pegar o toque poderia desligar o botão de novo, e a lista e o player então discordavam.

## 2.0.0-beta.9

- **Pulos rápidos durante um anúncio ou recarregamento**: a música que seus toques apontam toca assim que o anúncio ou recarregamento termina, e a tela mostra essa mesma música (o som e a tela não se separam mais); "anterior" durante um anúncio não reinicia mais o anúncio.
- **Intervalos de anúncios são parados na fonte**: os próprios gerenciadores de anúncios da página do player (de áudio e in-stream) são desligados do app, e novamente cada vez que um anúncio aparece, em vez de remendar os próprios arquivos do web player (como o SpotX faz, feito aqui em tempo de execução para que uma atualização da página não possa quebrá-lo). O dispositivo também não diz mais ao Spotify que não pode ler mp3 (`file_urls_mp3`) — o registro pelo qual algumas contas gratuitas eram recusadas (`402 COMMAND_FAILED`).
- **Sem mais silêncio até uma reinicialização**: um mudo de anúncio cujo desmudo foi perdido (a página nunca relata o fim de um intervalo) é levantado após 75 s, o mudo só é confiado quando chegou aos elementos de mídia, e um podcast travado é pego como uma música.
- **A página de login não pode mais girar para sempre**: o app não espera mais apenas pelo endereço; sem token assinado 25 s após a página do player voltar, a página de login é mostrada de novo para tentar.
- **A página se reconstrói após uma falha do renderer** (o relatado "o app travou", "sem som até reiniciar o player") em vez de deixar uma página morta para trás.
- **Uma identidade de cliente entre inicializações**: o id do dispositivo do token do cliente é mantido, então o Spotify não vê um novo cliente a cada início.
- **Seu telefone diz ao Spotify seu próprio nome**: o registro do dispositivo agora carrega o nome do telefone (aquele que o Android conhece), então a lista de outros dispositivos mostra ele em vez de um genérico, e o registro é exatamente o que o verdadeiro web player envia (sem diferença de formato de anúncio que o destaque).
- **Cada instalação parece seu próprio navegador**: a versão do navegador e o sistema que alega são desenhados uma vez por instalação em vez de serem a mesma string em todo telefone. Uma única regra poderia ter coberto todos os NEXTFY de uma vez; agora cobre um cliente, como um navegador real faria.
- **Um login que não vai a lugar nenhum deixa um rastro**: cada etapa de login chega ao log (`login step #n POST /login/v3/…`) com o status de qualquer coisa que o Spotify recuse, em vez de terminar em um carregador sem nada para ver.
- **Um relatório que você pode enviar**: Configurações → **Exportar um relatório de diagnóstico** grava o log recente do app (a página do player, o relay, os anúncios, as recusas) e os detalhes do dispositivo em um arquivo e abre a aba de compartilhamento — o log precisava de `adb` até agora, então um relatório de uma recusa ou um login que falha era impossível de entregar.
- **Capas e fotos de perfil que nunca carregaram são corrigidas**: os endpoints de perfil entregam identificadores `spotify:image:…` em vez do endereço de uma foto, e o carregador de imagem recebeu esse identificador como estava. Playlists públicas de um usuário e sua foto de perfil agora mostram suas capas.
- **Letras futuras são legíveis em um cartão escuro**: elas eram desenhadas em preto a 55 % de opacidade, invisíveis no cartão AMOLED; agora seguem o próprio fundo do cartão.
- **Durante um anúncio a tela diz isso** em vez de deixar a música anterior presa em "carregando", e o cabeçalho do player mantém o próprio nome da playlist quando o rótulo do Spotify volta vazio; a página do artista tem seu próprio menu de opções de novo e o separador relatado como faltando no cabeçalho do perfil está de volta.
- **Sem mais faixa de luz sob o app**: a barra de navegação foi deixada ao sistema (uma faixa branca com ícones escuros sob uma UI sempre escura) — agora se mistura.
- **Crossfade, para valer desta vez**: o player tinha um stub que sempre respondia "sem crossfade" e a configuração havia sido removida, então o controle deslizante que os testadores procuravam não fazia nada. Configurações → Reprodução agora oferece Desligado / 3 / 6 / 9 / 12 s: a música atual desvanece nos seus últimos segundos e a próxima entra desvanecendo — um player, um stream, então os dois nunca se sobrepõem, e o rótulo diz isso. Dois fail-safes independentes colocam o volume de volta, e nenhum caminho de saída pode deixar o som baixo.
- **Rolar o player não abre mais as letras**: qualquer arrasto para cima contava como o atalho de deslizar; agora exige um deslizar deliberado, julgado na liberação.
- **Um interruptor de Connect não reinicia mais a música**: quando outro dispositivo assumiu a reprodução e o NEXTFY a recupera, a posição é lembrada e restaurada se a página voltou ao início.
- **A notificação acompanha em segundo plano**: o loop de atualização esperava por um dispositivo que assumiu a reprodução, o que apenas um pull-back define — então um app iniciado em segundo plano nunca a atualizava. Agora roda sempre que o serviço está ativo.
- **Arquivos Locais lista o que outros players listam**: a consulta pegava apenas o que o índice do Android chama de música de 30 s ou mais, então um telefone cheio de gravações ou downloads mostrava "seus arquivos de áudio aparecerão aqui" enquanto outro player os listava (reportado: 29 faixas). Tudo acima de 20 s agora conta, menos Toques/Notificações/Alarmes/Android — 59 arquivos listados no telefone de teste onde 77 existem.
- **Remova uma música de uma playlist, e adicione-a a outra**: uma playlist que você pode editar agora oferece « Remover desta playlist » no menu da própria música (com Desfazer), e « Adicionar a outra playlist » abre um seletor das suas playlists com um campo de busca.
- **Pare todos os downloads**: uma ação cancela tudo que está rodando ou esperando **sem deletar um único arquivo** (« Remover todos os downloads » ainda é o separado e destrutivo).
- **Atualizações nos seus termos**: uma configuração desliga a verificação automática (fica ligada por padrão) e « Verificar atualizações agora » está ali mesmo.
- **O player mini nomeia seus fones**: ícone de Bluetooth e nome do dispositivo (ou « Fones » para com fio) quando o som não vem do telefone.
- **Android Auto funciona de início frio**: a árvore do carro está ali de imediato e uma busca ou reprodução espera pelo player em vez de falhar enquanto carrega.
- **O player mantém sua cor no AMOLED**: o tom da cor da capa costumava desaparecer no modo AMOLED; agora está em ambos, como pedido.
- **Uma música que o Spotify marca como indisponível ainda é tentada**, e se realmente não puder tocar o app diz « Não disponível em sua região » em vez de deixar um toque que não faz nada.

- **Podcasts baixam sem o SpotiFLAC e tocam como no app oficial**: um episódio agora baixa com uma conta gratuita — o arquivo é o **MP3 simples do editor** (`passthroughUrl`, resolvido logo antes de cada tentativa), baixado em partes com retomada, verificado (tipo, cabeçalho, tamanho), criptografado e mantido na loja offline sob `episode:<id>`. Exclusivos do Spotify (`passthrough = NONE`, Widevine-criptografado) são **recusados com uma mensagem clara**, DRM nunca é tentado. Uma fila própria, um worker, retomado na inicialização.
- **Páginas de show e episódio**: um cabeçalho com a capa e seu gradiente, editor/autores/narradores, classificação, **Seguir**, Episódios/Capítulos e Sobre, ordenar mais novo/mais velho, linhas de episódio com "11 min restantes" e uma marca "tocado"; a página do episódio tem suas ações (✓, baixar, compartilhar, ⋮, Tocar) e **"Marcar como tocado / não tocado"**, gravado na coleção da conta (para segui-lo). Uma página de audiolivro não está mais vazia: capítulos mais autores e narradores.
- **Um player de podcast próprio**: −15 s / +15 s, velocidade 1 → 3×, um relógio que alterna decorrido/restante, temporizador de sono, sem embaralhar ou repetir. Duas causas de episódios parando em segundo plano são corrigidas: o guardião de autoplay pausava episódios, e uma retomada que a página ignorava nunca era pedida de novo.
- **Os gestos do app oficial**: tocar a aba ativa novamente rola **Home** e **Sua Biblioteca** (lista e grade) de volta ao topo sem recarregar, tocar **Busca** novamente foca o campo; tocar a **capa** no player muda para uma **tela cheia imersiva** (controles dobrados em uma tira), desfeito por outro toque — o deslize rápido para as letras ainda funciona.
- **Recents, como no app oficial**: a entrada sai da Sua Biblioteca para o painel de perfil, sob "Seu recap". A página tem um cabeçalho "Recents" com voltar, pílulas **Música / Podcasts**, seções datadas (Hoje, Ontem, então a data) e linhas **agrupadas por container** — "4 faixas tocadas • EP • …", "9 faixas tocadas • Playlist", "12 faixas adicionadas • Playlist" para Curtidas (selo de coração), um artista com sua imagem redonda, um Blend — capas de 56 dp, um chevron redondo para **desdobrar** as faixas do container, um toque na linha para abri-la. As contas vêm da própria página (grupos, tipo de conteúdo, "adicionado" vs "tocado"), não um palpite, e correspondem ao app oficial na mesma conta.
- **Uma barra de fundo parecida com a do Spotify**: os ícones foram declarados maiores mas um componente Material os cortava para 24 dp, as abas ficavam no meio da barra em vez de se espalharem igualmente, e o rótulo selecionado podia ficar cinza sob um ícone branco. A barra agora usa três abas iguais na largura total, ícones de 28 dp, rótulos médios de 11 sp, cinza ocioso do Spotify, e o tom selecionado em ambos ícone e rótulo — sobre a barra de gestos do sistema.
- **Fazer login não mente mais**: a etapa de e-mail do Spotify envia a página de volta para open.spotify.com, e o app tomava aquele endereço como uma sessão — então sair para ler o e-mail terminava em um player anônimo sem nada tocável. A página de login agora fica na frente até que um token real exista, o curto período de graça conta apenas enquanto o app está na tela, voltar rele o token, e um token anônimo envia você para a página de login em vez de fingir.
- **O app se apresenta como um navegador real, de forma consistente**: o user agent agora carrega a versão reduzida que todo desktop Chrome envia (sem número de build nele), a lista de marcas segue a versão como o Chromium constrói, e o que é lembrado por instalação é quão atrás do WebView ele fica — então a afirmação segue atualizações do WebView em vez de desviar.
- **Arrastar uma música para a borda da fila rola a lista**: o reordenar funcionava apenas entre as linhas visíveis; um arrasto perto do topo ou de baixo agora rola a folha (velocidade crescendo em direção à borda) e a música pode ser movida por toda a fila.
- A linha de log `login=` (significava apenas "a página está em um URL de login") agora é `session=signed-in|anonymous|login-page`, lida do token; uma solicitação repassada agora carrega os cookies da página e os próprios cabeçalhos de transporte do navegador.
- **Menos recarregamentos desnecessários**: uma página que perde uma resposta (um render longo, uma linha principal ocupada) não é mais tomada como morta — o recarregamento espera por dois silenciosos seguidos, então um soluço curto não custa um recarregamento completo da página.
- **Um episódio preso no final continua para o próximo** em vez de começar de novo de 0:00 (a recuperação de travamento só sabia de músicas).
- **As cores do Spotify em todos os lugares, e movimento mais suave**: interruptores, controles deslizantes, botões de opção, caixas de diálogo e giradores pegavam as cores do papel de parede do telefone (ou roxo do Material); agora usam o verde e os cinzas do Spotify. Capas desvanecem em vez de aparecer, play/pause se transforma em vez de trocar, a cor do player mini desvanece de uma música para a próxima, o player de podcast pega a cor da capa, linhas desdobradas do Recents deslizam para dentro, e o desvanecimento da barra de fundo é denso o suficiente para que capas não apareçam mais através dos rótulos das abas. O botão Tocar da página de episódio agora vira Pausa enquanto toca.

## 2.0.0-beta.8

- **SpotiFLAC**: quando não está verificado (ou a verificação expirou), Downloads e qualquer download abrem a verificação primeiro, depois as músicas baixam; sua página é em tela cheia (Repetir e Pular estavam escondidos sob o player mini); sessões seguem as regras do SpotiFLAC Mobile (renovado uma vez, em sua última hora, e um download recusado porque outro acabou de renová-lo é enviado de novo), então muitos downloads de uma vez não encerram mais a verificação; você é informado quando deve ser feito novamente.
- **Downloads automáticos esperam por Wi-Fi**: listas baixadas permanecem sincronizadas apenas em Wi-Fi; um download que você inicia em dados móveis continua com um aviso.

## 2.0.0-beta.7

- **Música que continua tocando**: uma página de player congelada é pega e recarregada em segundos, e a música continua onde estava; sem mais músicas silenciosas após um pular, "anterior" ou um anúncio; reproduzir logo após o lançamento funciona.
- **Pulos cerca de três vezes mais rápidos** (~0,8 s do toque ao som em vez de ~2,5 s), e muito menos trabalho em segundo plano: muito menos chamadas para a página do player e para o Spotify.
- **Anúncios**: um anúncio que passa é silencioso, e intervalos de anúncios são mais curtos.
- **NEXTFY toca neste telefone novamente** quando o app do Spotify (ou outro dispositivo) assumiu a reprodução sem ser escolhido aqui; contas gratuitas recusadas pelo Spotify em alguns países recebem um fallback e uma mensagem.
- **Canvas**: o player abre direto nele, com um desvanecimento suave enquanto ainda está carregando; toque nele para assistir sozinho. Sem mais botões piscando.
- **Páginas de artistas e playlists mais rápidas**; suas **curtidas por artista** vêm primeiro e abrem sua própria página; corações estão certos em listas longas.
- **Smart shuffle** mantém a música tocando, adiciona músicas a todas as playlists e tem seu próprio ícone.
- **Novo**: seguidores e seguindo em perfis, episódios em playlists, músicas indisponíveis acinzentadas, barra de rolagem rápida, compartilhar como imagem (stories do Instagram), deslizar para cima para as letras, player amigável para dobráveis.
- **Configurações**: autoplay, cor de destaque, rotação de tela.
- **Novos idiomas**: alemão, espanhol, português e grego.
- **GrapheneOS**: o player carrega com "secure app spawning" ligado.
- **GitHub Sponsors**: apoie o NEXTFY no GitHub Sponsors também, ao lado do Ko-fi.

## 2.0.0-beta.6

- **Menos anúncios no exterior**: anúncios de servidores de anúncios de outros países são bloqueados, playlists patrocinadas e prateleiras de promoção são ocultas; anúncios são reconhecidos mesmo quando parecem músicas; um anúncio que ainda passa é silenciado e pulado.
- **Player mais estável**: tocar, pausar e pular vão um por vez (toques rápidos não travam mais), e uma música que não começou é pedida de novo.
- **Sem mais "estalo" entre músicas**: um desvanecimento muito curto em cada pular.
- **Smart shuffle a partir do player**: toque duas vezes em embaralhar em uma playlist ou Curtidas.
- **Controle remoto**: com outro dispositivo tocando, a notificação e o controle deslizante acompanham.
- **Temporizador de sono "fim da faixa"** funciona com episódios, arquivos locais e repetir uma; páginas de artistas carregam toda a discografia.
- **Discografia de artistas** (Lançamentos) mostra álbuns e singles novamente.
- **Nomes longos cabem**: nomes longos de playlists e mixes de artistas não empurram mais o layout; títulos grandes encolhem para caber, cards em uma linha mantêm a mesma altura.
- **Uma pausa permanece uma pausa**, mesmo logo no final de uma música.
- **Downloads estão seguros**: uma lista de downloads danificada não exclui mais suas músicas baixadas.
- **Mudanças que o Spotify recusa são desfeitas na tela** (curtidas, visibilidade de playlist), com uma mensagem.
- **Busca** mantém sua consulta quando você volta, e diz quando está carregando, vazia ou falhou (com Tentar novamente). Páginas de artista e perfil podem ser recarregadas também.
- **Jam**: participe com um link copiado; compartilhar um episódio funciona.
- **Sair** para tudo (músicas locais, vídeo, notificação); o economizador de dados se aplica desde o lançamento.
- Botões do player são nomeados para TalkBack.

## 2.0.0-beta.5

- **Downloads criptografados**: músicas baixadas só tocam no NEXTFY, no seu telefone.
- **Fila offline**: a fila de músicas baixadas mostra o que realmente toca a seguir, e pode ser editada.
- **Músicas baixadas não são mais reportadas ao Spotify**: sem mais álbum começando sobre uma música baixada.
- **Atualizações no app**: baixe e instale sem sair do app.
- **Leitura de QR do Jam não trava mais**; um banner avisa quando downloads precisam da verificação do SpotiFLAC.
- **Suas playlists, editadas no app**: nome, descrição, capa (das suas fotos), pública, privada ou colaborativa, excluir.
- **Perfis**: procure pessoas, abra um perfil (seguidores, playlists públicas), siga; o proprietário de uma playlist abre o deles.
- **Mais do Spotify**: "Não tocar este artista", salvar episódios, sugestões de busca, "Mais parecido com isto" abaixo de álbuns, biblioteca ordenada por recentes ou adicionados recentemente, prenda e mova itens para pastas.
- **Ordenar em ambas as direções** (toque no sort escolhido novamente), lembrado para cada playlist.
- **Correções**: preto AMOLED em todos os lugares, listas não terminam mais sob o player mini, vídeos musicais em tela cheia aparecem, a página da conta do Spotify fica sob a barra de status, "Salvo em" confirma cada mudança, pulos rápidos não travam mais o player, sem tela branca em telefones em modo claro.
- **Correções de segurança e confiabilidade** (Android Auto, páginas de biblioteca, sair, links, buscas de vídeo).

## 1.1.1

- **Economizador de dados** (menu de perfil): em dados móveis o NEXTFY não usa nenhum dado. Apenas músicas baixadas e Arquivos Locais tocam, downloads esperam por Wi-Fi, e o player do Spotify volta sozinho no Wi-Fi.
- **Encontrar em uma lista**: busque dentro de Curtidas, uma playlist ou Arquivos Locais (título, artista ou álbum).
- **Sua Biblioteca**: mover uma playlist (segure, depois arraste) funciona, na lista e na grade; o arrasto parava assim que começava.
- **Configurações** abrem em tela cheia, então a lista rola sem mover a folha.

## 1.1.0

- **Downloads**: baixe uma música (do menu dela), um álbum, uma playlist ou Curtidas, e ouça sem conexão, de **Baixados** na Sua Biblioteca ou da página do álbum ou playlist.
  - O áudio vem do YouTube Music, como o YouTube serve (sem re-encodificação). Cada música é verificada contra a gravação original; quando nenhuma corresponde, a música é marcada como não baixável em vez de salvar a errada.
  - Arquivos ficam dentro do NEXTFY. Um álbum ou playlist baixado permanece em sincronia: músicas adicionadas são baixadas, músicas que saíram (um Blend renovado, uma música removida) são deletadas, a menos que outra lista baixada ou seu próprio download as mantenha.
  - Depois que uma lista é baixada, seu botão vira uma lixeira. Em Baixados, deslize uma música da direita para a esquerda para removê-la, ou remova tudo do menu de perfil.
- **Pastas**: Sua Biblioteca mostra suas pastas do Spotify e as abre nível por nível. O + no topo cria uma playlist, uma pasta (uma pasta real do Spotify) ou uma playlist neste dispositivo; uma pasta pode ser renomeada.
- **Ordenar e reordenar Sua Biblioteca**: ordene por ordem personalizada, alfabética ou criador; segure uma playlist, depois arraste para movê-la (mantido neste telefone). Curtidas, Arquivos Locais e Baixados ficam no topo.
- **Arquivos locais em playlists**: o + de uma música em Arquivos Locais a adiciona a uma playlist neste dispositivo, ou a uma das suas playlists do Spotify, em **Adicionado neste dispositivo** (o Spotify não pode receber arquivos do telefone, então eles ficam no telefone).
- **O + de uma música**: o primeiro toque salva em Curtidas; tocar o check verde então abre **Salvo em** para adicionar às suas playlists. Salvo em lista apenas as playlists que você pode mudar, e diz quando o Spotify recusa uma mudança.
- **Player**: puxe-o de cima para fechá-lo, como o botão de voltar.
- **Vá para rádio da música** no menu de cada música (player, busca, playlists…).
- **Vídeos musicais**: um título com apóstrofo ("J'SAVAIS") não recebe mais o clipe de outra música; uploads instrumentais são pulados.
- **Home** na barra de fundo sempre abre o Home.
- **Busca**: playlists nos resultados, e filtros para músicas, artistas, álbuns e playlists.
- **Podcasts e audiolivros estão ocultos por enquanto**; voltarão em uma versão posterior.
- **Páginas de artistas**: a discografia inteira, não apenas os lançamentos que o Spotify destaca.
- **Idioma**: francês em um telefone francês, inglês em qualquer outro, sem mais idiomas misturados. **Idioma do app** no menu de perfil escolhe outro (os rótulos do Spotify são traduzidos, os próprios textos do NEXTFY permanecem em inglês).
- **Player mais estável e leve**: o player oculto do Spotify permanece em uma versão testada mantida no telefone, então uma atualização do site do Spotify não pode mais quebrar o app da noite para o dia, e não carrega mais capas, loops de Canvas e fontes que ninguém vê (cerca de metade da memória e um quarto das solicitações na inicialização).
- **Reiniciar o player** no menu de perfil, para quando a música para de responder.
- **Economizador de bateria**: o próprio economizador do NEXTFY é independente do do Android (que já reduz a taxa da tela) e apenas segue seu interruptor. O tema AMOLED também é uma escolha própria, e voltar ao app não busca mais um vídeo musical à toa.
- **Crossfade**: nenhum trabalho em segundo plano quando está desligado, e a configuração é mantida quando o player recarrega.
- **Sobre** no menu de perfil: a versão, o [servidor do Discord](https://discord.gg/jXKnGVFnGS) e o código-fonte.
- **Apoie o NEXTFY**: o NEXTFY é gratuito e não tem anúncios; agora você pode apoiá-lo no Ko-fi do menu de perfil ou Sobre. Um lembrete curto aparece um dia após instalar esta versão, depois a cada 2 semanas; "Não, obrigado" fecha, e nada fica atrás de uma doação.
- Novas versões são verificadas a cada 3 horas em vez de uma vez por dia.
- Sua Biblioteca ainda abre Arquivos Locais e Baixados sem conexão, e a notificação mostra a capa de músicas locais e baixadas.

## 1.0.4

- **Arquivos Locais**: uma pasta na Sua Biblioteca, logo abaixo de Curtidas, com a música armazenada no seu telefone. Ela toca no app como as demais (player, player mini, notificação, tela de bloqueio, botões do fone de ouvido).
- **Spotify Connect**: a notificação agora acompanha as mudanças de faixa no outro dispositivo; podia ficar na música anterior.
- Menos atualizações do player enquanto toca em outro dispositivo (menos CPU e bateria).
- Sem botão de curtir em arquivos locais.
- Um servidor de anúncios do Spotify a mais bloqueado.
- A janela de atualização mostra as novidades.

## 1.0.3

- **Spotify Connect**: a música não volta mais para o telefone nem para sozinha.
  - Reproduzir de Curtidas vai para o dispositivo que está tocando.
  - Deslizar o app para fora não pausa mais um dispositivo Connect.
  - Uma sessão Connect fechada pelo sistema não traz mais a reprodução de volta para o telefone.
- **Crossfade** (Configurações, 0–12 s): o final de uma faixa desvanece e a próxima entra desvanecendo.
- **Reprodução em segundo plano**: o NEXTFY pede uma vez para levantar a otimização de bateria, então música e Connect continuam com a tela desligada. Também em Configurações.
- **Android Auto** funciona novamente: Curtidas, playlists, álbuns, artistas, busca e reprodução por voz.
- **Android 10 e 11** são suportados.
- A janela de atualização combina com o estilo do app.

## 1.0.2

- Nova tela de boas-vindas antes de fazer login, e uma nova tela de falha.
- Toda tela segue o idioma do telefone (rótulos oficiais em 73 idiomas, inglês e francês para os próprios textos do NEXTFY).
- Novos ícones, novo ícone de notificação, telas antigas removidas.

## 1.0.1

- **Spotify Connect**: a notificação continua acompanhando o outro dispositivo com a tela desligada.
- Reabrir o app não move mais a reprodução de volta para o telefone.

## 1.0.0

- Primeira versão: Spotify com conta gratuita e sem anúncios, interface nativa, letras sincronizadas, vídeos musicais oficiais, Spotify Connect.
