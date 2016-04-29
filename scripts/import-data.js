#!/usr/bin/env node

/**
 * This is a node.js script for fetching data from Marvel API and populate a sqlite DB to be
 * bundled together with the app, so user doesn't need to fetch all characters data after
 * app installation.
 */

var util = require('util'),
	opt = require('node-getopt'),
	api = require('marvel-api');

var opt = opt.create([
  ['' , 'public_key=ARG' , 'Comma-separated list of profile ids'],
  ['' , 'private_key=ARG'       , 'Comma-separated list of roles to add to the given profiles'],
  ['' , 'db_path=ARG'         , 'The Brain environment. Valid values are: local, dev, tst and prd.']
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

var marvel = api.createClient({
  publicKey: opt.options.public_key,
  privateKey: opt.options.private_key
});

marvel.characters.findAll()
  .then(console.log)
  .fail(console.error)
  .done();