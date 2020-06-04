'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
exports.sendNotification = functions.database.ref('/Notifications/{receiver_user_id}/{notification_id}')
.onWrite((data,context) =>
{
    const receiver_user_id = context.params.recevier_user_id;
    const notification_id = context.params.notification_id;
    console.log('we have a nofication to sent to: ' , receiver_user_id);


    if(!data.after.val()){
        console.log('a notficication has been deleted: ' , notification_id);
        return null;
    }
    const DeviceToken = admin.database().ref(`/Users/${receiver_user_id}/device_token`).once('value');
    return DeviceToken.then(result => {
        const token_id = result.val();
        const payload = {
            nofication:{
                title: "New Chat Request",
                body: `you have a new Chat Request, Please Check.`,
                icon: "default"

            }
        };

        return admin.messaging().sendToDevice(token_id,payload).then(response => console.log('This was a notification feature. '));
    });
});


