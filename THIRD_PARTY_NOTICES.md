# Avisos de terceiros

O NEXTFY é software livre sob a GNU General Public License v3.0 (veja `LICENSE`).
Ele é construído com os projetos abaixo; as licenças deles são compatíveis com o GPL-3.0.

## Spotui — GPL-3.0
https://github.com/Spotui/Spotui

A remux DASH-to-FLAC (`base/app/src/main/java/com/music/spotui/data/offline/DashFlacRemux.kt`)
é portada do Spotui e permanece sob a GPL-3.0. O arquivo diz isso no cabeçalho.

## BitChord — GPL-3.0-or-later
https://github.com/kushagrasinghx/BitChord (commit `edc1e6f`)
### Tutoriais

- Criar arquivo
- Criar diretório
- Escrever conteúdo
- Editar conteúdo
- Apagar arquivo
- Mover arquivo
- Criar link simbólico

Por contribuintes do BitChord. O visual Immersive é portado dele. Estes arquivos em
`base/app/src/main/java/com/music/spotui/ui/look/` são adaptados do BitChord, permanecem sob a
GPL-3.0-or-later, e dizem isso no cabeçalho:

- `MeshGradient.kt`, de `sharedUi/.../ui/player/MeshGradient.kt` (o pano de fundo de malha e `cyclingMeshPalette`)
- `ThinSlider.kt`, de `sharedUi/.../ui/player/ThinSlider.kt` (a barra de busca fina, sem Automix)
- `LyricClock.kt`, de `sharedUi/.../ui/player/LyricClock.kt` (o indicador de letra suavizado)
- `SweptLyricLine.kt`, de `sharedUi/.../ui/player/PlayerLyrics.kt` (`SweptLyricLine`, seus auxiliares
  de desenho e constantes) e `sharedUi/src/androidMain/.../ui/player/PlayerPlatformUi.android.kt`
- `SweptLyricModel.kt`, de `shared/.../data/lyrics/LyricLine.kt` (tempo de palavra, elevação e brilho)

A UI Immersiva em `base/app/src/main/java/com/music/spotui/ui/immersive/` (player, letras, fila,
barra de abas, barras superiores, folhas, movimento, componentes, Home, Busca, Biblioteca, Categoria, álbum/playlist, artista, perfil, podcast e páginas de download) é
adaptada destes arquivos do BitChord, cada um nomeado no cabeçalho do arquivo do NEXTFY que o carrega:
`sharedUi/.../ui/player/` NowPlayingScreen, PlayerControls, PlayerState, PlayerHost, PlayerLyrics,
PlayerQueue; `sharedUi/.../ui/components/` Common, SearchField, Skeletons, SongText, OptimizedHaze;
`sharedUi/.../ui/screens/` HomeScreen, SearchScreen, LibraryScreen, ExploreScreen;
`app/.../ui/components/` MiniPlayer, FrostedTopBar, GlassNavBar, BottomFadeScrim,
floatingtabbar/FloatingTabBar, floatingtabbar/GlassSelectionPill; `app/.../ui/player/PlayerSheetMotion`;
`app/.../ui/screens/` DetailScreen, LibraryPhoneParts; `app/.../ui/utils/IosOverscroll`.

Nenhuma fonte, ícone, imagem ou marca do BitChord está incluída.

Código que o BitChord carrega de outros projetos, mantido com seu próprio cabeçalho:
- `shell/FloatingTabBar.kt`: compose-floating-tab-bar por Elyes Mansour
  (https://github.com/elyesmansour/compose-floating-tab-bar), Apache-2.0, através do Echo-Music
  (https://github.com/EchoMusicApp/Echo-Music), GPL-3.0.
- `shell/GlassSelectionPill.kt`: o movimento da pílula segue liquid_glass_easy
  (https://pub.dev/packages/liquid_glass_easy), MIT License, Copyright (c) 2025 Ahmed Gamil.
- `components/ImmersiveOverscroll.kt`: do Convx (https://github.com/cosmictaserdev-creator/Convx), GPL-3.0.
- `shell/ImmersiveGlass.kt` (Liquid Glass): adaptado do `app/.../ui/components/LiquidGlass.kt` do BitChord,
  ele próprio adaptado do GlassEffectConfig / `Modifier.liquidGlass` do Echo-Music
  (https://github.com/EchoMusicApp/Echo-Music), GPL-3.0. A lente de `shell/GlassSelectionPill.kt` e
  o chrome minimizado de `shell/ImmersiveChrome.kt` seguem os arquivos `floatingtabbar/` do BitChord.

## Backdrop — Apache-2.0
https://github.com/Kyant0/backdrop (v2.0.0, `io.github.kyant0:backdrop`), Copyright 2025 Kyant0.

A refração, desfoque, realce de borda e sombra do Liquid Glass do visual Immersive. Vendado como
fonte em `base/app/src/main/java/com/music/spotui/ui/immersive/shell/backdrop/`, tirado da
cópia vendada do BitChord (que adiciona uma escala de resolução de backdrop e um backdrop exportado, usado para
manter o custo de GPU baixo; o artefato publicado não tem nenhum). Cada arquivo mantém seu cabeçalho Apache-2.0;
o pacote é renomeado. Texto da licença em `base/licenses/Apache-2.0.txt` (também cobre Haze e
compose-floating-tab-bar acima).

## Haze — Apache-2.0
https://github.com/chrisbanes/haze

As barras fosfuscadas do visual Immersive (dependência do Gradle `dev.chrisbanes.haze:haze`).

## Inter — SIL Open Font License 1.1
https://github.com/rsms/inter

A fonte do visual Immersive (`base/app/src/main/res/font/inter.ttf`), Copyright 2020 The Inter
Project Authors. Reduzida para Latim com apenas seu eixo de peso. Texto da licença em
`base/licenses/Inter-OFL.txt`.

## Chromium `cast_channel.proto` — BSD-3-Clause
https://chromium.googlesource.com/chromium/src/+/main/components/cast_channel/proto/cast_channel.proto

Copyright The Chromium Authors. Transmissão para dispositivos Google Cast
(`base/app/src/main/java/com/music/spotui/cast/`) é o próprio remetente CastV2 do NEXTFY, sem o
Google Cast SDK. `CastMessage.kt` codifica à mão a mensagem `CastMessage` que este arquivo define
(seus números e tipos de campo); nenhum código do Chromium está incluído.
