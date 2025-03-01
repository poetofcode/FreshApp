const ObjectId = require("mongodb").ObjectId;
const crypto = require('crypto');
const { utils } = require('../utils');


class FeedRepository {

	constructor(context) {
		this.context = context;
		this.postsCollection = context.getDb().collection('posts');
	}

	async saveFeed(posts) {
		console.log('FeedRepository: logging');
		
		let bulkTags = [];
		
		posts.forEach(post => {
			post.createdAt = new Date();
			bulkTags.push({
				updateOne: {
					filter: { link: post.link },
					update: { $set: post },
					upsert: true
				}
			});
		});

        this.postsCollection.bulkWrite(bulkTags);
	}

}

exports.create = (context) => new FeedRepository(context);