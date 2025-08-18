package iccsoft_auth.request.iccsoftuser;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UMRequest {

    private Long id;
    private String username;
    private String name;
    private String email;
    private String password;
    private String adresse;
    private Long telephone;
    private String entreprise;
}
