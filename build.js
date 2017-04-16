var packager = require('electron-packager');
var electronInstaller = require('electron-winstaller');

var darwinOptions = {
    dir: './',
    name: 'AudioEqualizer',
    platform: 'darwin',
    arch: 'x64',
    electronVersion: '1.6.2',
    out: './releases',
    appBundleId: 'com.voiceinthesea.audioequalizer',
    appVersion: '0.0.1',
    overwrite: true,
    asar: false,
    icon: 'icon.icns',
    app_version: '0.0.1',
    bundle_id: 'com.voiceinthesea.audioequalizer',
    appname: 'VoiceInTheSeaAudioEqualizer',
    sourcedir: './resources',
    ignore: './releases'
};

var windowsOptions = {
    dir: './',
    name: 'AudioEqualizer',
    platform: 'win32',
    arch: 'x64',
    electronVersion: '1.6.2',
    out: './releases',
    appBundleId: 'com.voiceinthesea.audioequalizer',
    appVersion: '0.0.1',
    overwrite: true,
    asar: false,
    icon: 'icon.ico',
    app_version: '0.0.1',
    bundle_id: 'com.voiceinthesea.audioequalizer',
    appname: 'VoiceInTheSeaAudioEqualizer',
    sourcedir: './resources',
    ignore: './releases'
};

function createWindowsInstaller() {
    console.log('Packaging windows installer. This may take a while.');
    electronInstaller.createWindowsInstaller({
        appDirectory: 'releases/AudioEqualizer-win32-x64/',
        outputDirectory: 'releases/AudioEqualizer-win32-x64-installer/',
        authors: 'Vincent Pizzo',
        exe: 'AudioEqualizer.exe',
        description: 'An audio equalizer for Voice in the Sea products.'
    }).then(function() {
        console.log('Successfully created installer');
    }, function(e) {
        console.log('No dice', e);
    });
}

function packageWindows() {
    packager(windowsOptions, function(err, appPaths) {
        if (err) {
            console.error('Failed to package windows app', err);
        } else {
            console.log('Successfully packaged windows app', appPaths);
            createWindowsInstaller();
        }
    });
}

function packageDarwin() {
    packager(darwinOptions, function(err, appPaths) {
        if (err) {
            console.error('Failed to package darwin app', err);
        } else {
            console.log('Successfully packaged darwin app', appPaths);
            packageWindows();
        }
    });
}

packageDarwin();
