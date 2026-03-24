package server.mapper;


import server.dto.UserDTO;
import server.model.User;

public class UserMapper {
    public static UserDTO toDTO(User user)
    {
        return new UserDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
    public static User fromDTO(UserDTO dto)
    {
        return new User(
                dto.getUserId(),
                dto.getUsername(),
                dto.getEmail(),
                dto.getCreatedAt()
        );
    }
}
