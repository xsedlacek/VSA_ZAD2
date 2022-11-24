package sk.stuba.fei.uim.vsa.pr2.web.response.factory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private String message;
    private Boolean error;

    public static MessageDto buildError(String message){
        return new MessageDto(message,true);
    }



}
