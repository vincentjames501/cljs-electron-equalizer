# Equalizer using ClojureScript/Reagent/Electron

![](https://raw.githubusercontent.com/vincentjames501/cljs-electron-equalizer/master/demo.gif)

A basic equalizer written in ClojureScript using Reagent & Electron.

## Running it

```shell
gem install foreman              # install foreman gem (see Procfile)
npm install electron -g          # install electrob binaries

foreman start &                  # compile cljs and start figwheel
electron .                       # start electron
```

## Releasing

```shell
lein cljsbuild once frontend-release # compile ui code
lein cljsbuild once electron-release # compile electron initialization code

electron .                           # start electron to test that everything works
npm run package                      # package everything and create installers
```

After that you can follow [distribution guide for the electron.](https://github.com/atom/electron/blob/master/docs/tutorial/application-distribution.md)
