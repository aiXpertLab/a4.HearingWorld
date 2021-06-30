package com.hypech.SV3.event;

public class SexVoiceSelectEvent {


        private String message;

        public SexVoiceSelectEvent(String string){
            message = string;
        }
        public void setMessage(String message){
            this.message = message;
        }

        public String getMessage(){
            return message;
        }


}
