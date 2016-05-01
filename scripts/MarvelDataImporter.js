#!/usr/bin/env node

/**
 * This is a node.js script for fetching data from Marvel API and populate a sqlite DB to be
 * bundled together with the app, so user doesn't need to fetch all characters data after
 * app installation.
 */

var util = require('util'),
	  opt = require('node-getopt'),
	  api = require('marvel-api'),
    sqlite3 = require('sqlite3').verbose();

var LIMIT = 25;

var opt = opt.create([
  ['' , 'public_key=ARG' , 'Marvel API public key'],
  ['' , 'private_key=ARG', 'Marvel API private key'],
  ['' , 'db_path=ARG',     'Path to the SQLite DB to be populated with Marvel data.']
])
.bindHelp()
.parseSystem();

if (!opt.options.public_key) {
  process.stderr.write("Missing public_key argument.\n");
  process.exit(1);
}

if (!opt.options.private_key) {
  process.stderr.write("Missing private_key argument.\n");
  process.exit(1);
}

if (!opt.options.db_path) {
  process.stderr.write("Missing db_path argument.\n");
  process.exit(1);
}

var db = new sqlite3.Database(opt.options.db_path);

var marvel = api.createClient({
  publicKey: opt.options.public_key,
  privateKey: opt.options.private_key
});

crawlData(LIMIT, 1375);

function crawlData(offset, limit) {
  marvel.characters.findAll(offset, limit, function(err, results) {
    if (err) {
      console.log(util.inspect(err, { showHidden: true, depth: null }));
      return;
    }

    var characters = results.data;
    characters.forEach(function(character, index, array) {
      var thumbnail = getThumbnailUrl(character);


      console.log(character);

      var params = {
        '$id': character.id, 
        '$name': character.name, 
        '$description': character.description, 
        '$modified': character.modified, 
        '$details_url': getDetailsUrl(character), 
        '$thumbnail': getThumbnailUrl(character)
      };

      // db.run("INSERT INTO Character (id, name, description, modified, details_url, thumbnail) VALUES ($id, $name, $description, $modified, $details_url, $thumbnail)", params);
    });

    var totalProcessed = results.meta.offset + characters.length;
    if (totalProcessed < results.meta.total) {        
      console.log('============================');
      console.log('totalProcessed: ' + totalProcessed);
      console.log('total: ' + results.meta.total);
      console.log('count: ' + results.meta.count);
      console.log('============================');

      var newOffset = totalProcessed;
      // crawlData(LIMIT, newOffset);
    }
  });
}

function getThumbnailUrl(character) {
  var thumbnailUrl = '';
  if (character.thumbnail && character.thumbnail.path) {
    thumbnailUrl = character.thumbnail.path + '.' + character.thumbnail.extension;
  }

  return thumbnailUrl;
}

function getDetailsUrl(character) {
  var detailsUrl = '';
  if (!character.urls || character.urls.length == 0) {
    return detailsUrl;
  }

  character.urls.forEach(function(item, index, array) {
    if (item.type == 'detail') {
      detailsUrl = item.url;
    }
  });

  return detailsUrl;
}

function processRelatedSeries(character) {
  if (!character.series || !character.series || !character.series.length) {
    return;
  }

  character.series.forEach(function(series, index, array) {

  });
}

function processRelatedComics(character) {
  if (!character.comics || !character.comics || !character.comics.length) {
    return;
  }

  character.comics.forEach(function(comic, index, array) {
      var params = {
        '$character_id': character.name, 
        '$comic_id': character.description
      };

      // db.run("INSERT INTO Character (id, name, description, modified, details_url, thumbnail) VALUES ($id, $name, $description, $modified, $details_url, $thumbnail)", params);
  });
}