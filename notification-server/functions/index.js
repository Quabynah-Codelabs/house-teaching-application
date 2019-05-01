// Add packages
const functions = require('firebase-functions');
const admin = require('firebase-admin');

// Initialize Firebase Admin
admin.initializeApp();

// Store user information in the database
// exports.alertNewUser = functions.auth.user().onCreate((record, context) => {
//     // get the database instance
//     let db = admin.firestore();

//     // Get default profile image
//     var defaultAvatar = 'https://firebasestorage.googleapis.com/v0/b/house-teaching.appspot.com/o/default-avatar.png?alt=media&token=b1ab3898-f830-42b6-aa08-18c70627bb87';

//     // get the database path for users & we store the user's information there
//     return db.collection('users').doc(record.uid).set({
//         name: record.displayName ? record.displayName : 'No username',
//         email: record.email ? record.email : 'No email',
//         phone: record.phoneNumber ? record.phoneNumber : 'No phone number',
//         uid: record.uid,
//         token: null,
//         avatar: record.photoURL ? record.photoURL.toString() : defaultAvatar
//     }).then(() => {
//         return console.log(`User created as: ${record.email} & UID: ${record.uid}`);
//     }).catch(err => {
//         return console.log(`Could not create user document. ${err.message}`);
//     })
// });

// Send notification to the user's device
exports.sendNotification = functions.firestore.document('users/{uid}').onUpdate((change, context) => {
    // Extract the user's ID
    var uid = context.params.uid;

    // Compare the user's token
    var oldToken = change.before.data().token;

    if (change.after.exists && change.after.data().token === oldToken) {
        return console.log('User token has not changed');
    } else {
        var newToken = change.after.data().token;
        if (newToken) {
            // Notification body
            var payload = {
                data: {
                    title: 'New token found',
                    body: 'Your device token has been updated successfully',
                    token: newToken.toString(),
                    uid: uid
                }
            };

            // Send a notification to the user
            return admin.messaging().sendToDevice(newToken, payload).then(() => {
                return console.log('Notification sent to the device.', newToken);
            }).catch(err => {
                if (err) {
                    return console.log(err.message);
                } else return null;
            });
        } else {
            return console.log('Cannot send a notification to a null token');
        }
    }
});