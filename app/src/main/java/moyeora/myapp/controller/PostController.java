package moyeora.myapp.controller;

import lombok.RequiredArgsConstructor;
import moyeora.myapp.annotation.LoginUser;
import moyeora.myapp.dto.AjaxResponse;
import moyeora.myapp.security.PrincipalDetails;
import moyeora.myapp.service.CommentService;
import moyeora.myapp.service.PostService;
import moyeora.myapp.service.SchoolUserService;
import moyeora.myapp.util.FileUpload;
import moyeora.myapp.vo.AttachedFile;
import moyeora.myapp.vo.Comment;
import moyeora.myapp.vo.Post;
import moyeora.myapp.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/post")
@SessionAttributes("attachedFiles")
public class PostController {

    //  private static final Log log = LogFactory.getLog(PostController.class);
    private final PostService postService;
    private final FileUpload fileUpload;
    private final SchoolUserService schoolUserService;
    private  final  CommentService commentService;
    private String uploadDir = "post/";
    private static final Log log = LogFactory.getLog(PostController.class);


    @Value("${ncp.storage.bucket}")
    private String bucketName;


    @GetMapping("form")
    public void form() throws Exception {

    }


//스쿨 공지를 다루는 메서드


//  @PostMapping("fix")
//  @ResponseBody
//  public Object fixedPost(
//         @RequestBody Post post) throws Exception {
//
//
//
//      postService.fixedPost(post);
//      log.debug(postService.fixedPost(post) + "@@@@@@@@@@@@@@@@@@");
//
//      return AjaxResponse.builder().status("success").build();
//    }

  @PostMapping("commentAdd")
  public AjaxResponse commentAdd(
          @RequestParam String content,
          @RequestParam int postNo,
          Comment comment,
          HttpSession httpSession,
          @AuthenticationPrincipal PrincipalDetails principalDetails,
          @LoginUser User loginUser) throws Exception {

    try {

      comment.setPostNo(postNo);
      comment.setContent(content);
      comment.setContent("test");
      comment.setUserNo(loginUser.getNo());
      log.debug(httpSession.getAttribute("loginUser") + "@@@@@@@@@@@@@@@@@@");

      commentService.add(comment);

      // 성공적으로 고정을 등록했을 경우 응답을 생성합니다.
      return AjaxResponse.builder().status("success").build();
    } catch (Exception e) {
      // 글 고정에 실패한 경우 예외를 처리하고 에러 응답을 생성합니다.
      return AjaxResponse.builder().status("error").message("Failed to add comment").build();
    }
  }


  //  @GetMapping("list4")
//  public void list4(Model model, int schoolNo,
//                    HttpSession httpSession,
//                    @AuthenticationPrincipal PrincipalDetails principalDetails,
//                    @LoginUser User loginUser) {
//    httpSession.getAttribute("loginUser");
//    log.debug("@@@@@@@@@@@@@@@@@" + loginUser.getNo());
//
//    log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + loginUser);
  @PostMapping("fix")
  @ResponseBody
  public AjaxResponse fixedPost(Post post,
                                HttpSession httpSession,
                                @AuthenticationPrincipal PrincipalDetails principalDetails,
                                @LoginUser User loginUser) {


    loginUser.getNo();
    int userNo = post.getNo();
    int schoolNo = postService.findByPostSchoolNo(post.getNo());


    log.debug("schoolNo + @@@@@@@@@@@@@@@@@@@" + schoolNo);
    log.debug("postNo + @@@@@@@@@@@@@@@@@@@@@" + userNo);
    log.debug("sessionNo + @@@@@@@@@@@@@@@@@@@@@" + loginUser.getNo());


    int lNo = schoolUserService.findByUserLevelNo(schoolNo, userNo);

    log.debug("levelNo + @@@@@@@@@@@@@@@@@@@@@@@@" + lNo);


    try {

      if (lNo == 3 || lNo == 4) {



        // 메서드를 호출하여 글 고정을 하고 결과를 받아옵니다.
        postService.fixedPost(post);

        // 성공적으로 고정을 등록했을 경우 응답을 생성합니다.
        return AjaxResponse.builder().status("success").build();
      }
    } catch (Exception e) {
      // 글 고정에 실패한 경우 예외를 처리하고 에러 응답을 생성합니다.
    }
    return AjaxResponse.builder().status("error").message("Failed to fix post").build();
  }



  @PostMapping("fixCancel")
  @ResponseBody
  public AjaxResponse fixedCancel(Post post) {
    try {
      //메서드를 호출하여 글 고정 취소를 진행하고 결과를 받아옵니다.
      postService.fixedCancel(post);

      //
      return AjaxResponse.builder().status("success").build();
    } catch (Exception e) {
      // 고정 취소에 실패한 경우 예외를 처리하고 에러 응답을 생성합니다.
      return AjaxResponse.builder().status("error").message("Failed to fix cancel post").build();
    }
  }





  @GetMapping("noticelist")
  public void list(Model model, int schoolNo) {


    System
            .out.println(postService.findByNotice(schoolNo));
    log.debug(postService.findByNotice(schoolNo));

    log.debug(schoolUserService.findBySchoolUserList(schoolNo));
    System.out.println(schoolUserService.findBySchoolUserList(schoolNo));
    List<Post> post = postService.findByNotice(schoolNo);

    System.out.print("@@@@@@@@@@@@@@@@@@@" + post);
    model.addAttribute("schoolNo", schoolNo);
    model.addAttribute("noticelist", post);
    model.addAttribute("schoolUsers", schoolUserService.findBySchoolUserList(schoolNo));
  }


//공지글 리스트 url
  @GetMapping("noticeview/{lNo}")
  @ResponseBody
  public Object findByNotice(int no, @PathVariable String lNo) throws Exception {

    int schoolNo = Integer.parseInt(lNo);
    log.debug(postService.get(no, schoolNo));
    Post post = postService.get(no, schoolNo);
    List<AttachedFile> attachedFiles = postService.getAttachedFiles(no);
    List<Comment> comments = postService.getComments(no);

    if (post == null) {
      throw new Exception("게시글 번호가 유효하지 않습니다.");
    }
    System.out.println(post);
    System.out.println(attachedFiles);
    System.out.println(comments);

    Map<String,Object> result = new HashMap<>();
    result.put("comments", comments);
    result.put("files", attachedFiles);
    result.put("post", post);
    return result;
  }

//공지게시판
  @PostMapping("noticeadd")
  public String addNotice(
          Post post,
          MultipartFile[] files,
          HttpSession session) throws Exception {

    // 파일 업로드 및 AttachedFile 생성
    List<AttachedFile> attachedFiles = new ArrayList<>();
    for (MultipartFile file : files) {
      if (file.getSize() == 0) {
        continue;
      }
      String filename = fileUpload.upload(this.bucketName, this.uploadDir, file);
      // AttachedFile 객체 생성 후 파일 이름 설정
      AttachedFile attachedFile = new AttachedFile();
      attachedFile.setFileName(filename);
      attachedFiles.add(attachedFile);
    }





    // 'created_at' 필드에 현재 시간 설정
    post.setCreatedAt(new Date()); // 이 코드는 java.util.Date를 import 해야 합니다.

    // 나머지 처리 코드
    post.setCreatedAt(new Date());
    postService.addNotice(post);

    return "redirect:noticelist?schoolNo=" + post.getSchoolNo();
  }


  //



    @PostMapping("add")
    public String add(
            Post post,
            HttpSession session,
            SessionStatus sessionStatus) throws Exception {

//        User loginUser = (User) session.getAttribute("loginUser");
//    if (loginUser == null) {
//      throw new Exception("로그인하시기 바랍니다!");
//    }
//
//    Post old = postService.get(post.getNo());
//    if (old == null) {
//      throw new Exception("번호가 유효하지 않습니다.");
//
//    } else if (old.getNo() != loginUser.getNo()) {
//      throw new Exception("권한이 없습니다.");
//    }


        // 게시글 등록할 때 삽입한 이미지 목록을 세션에서 가져온다.
        List<AttachedFile> attachedFiles = (List<AttachedFile>) session.getAttribute("attachedFiles");


        // attachedFiles가 null이 아닌지 확인
        if (attachedFiles != null) {
            // attachedFiles가 null이 아닐 경우에만 처리

            for (int i = attachedFiles.size() - 1; i >= 0; i--) {
                AttachedFile attachedFile = attachedFiles.get(i);
                if (post.getContent().indexOf(attachedFile.getFilePath()) == -1) {
                    // Object Storage에 업로드 한 파일 중에서 게시글 콘텐트에 포함되지 않은 것은 삭제한다.
                    fileUpload.delete(this.bucketName, this.uploadDir, attachedFile.getFilePath());
                    log.debug(String.format("%s 파일 삭제!", attachedFile.getFilePath()));
                    attachedFiles.remove(i);
                }
            }
            if (attachedFiles.size() > 0) {
                post.setFileList(attachedFiles);
            }
            // 게시글을 등록하는 과정에서 세션에 임시 보관한 첨부파일 목록 정보를 제거한다.
            sessionStatus.setComplete();
        }

        // 'created_at' 필드에 현재 시간 설정
        post.setCreatedAt(new Date()); // 이 코드는 java.util.Date를 import 해야 합니다.

        // 나머지 처리 코드
        log.debug("@@@@@@@===>>" + post);
        postService.add(post);

        return "redirect:list?schoolNo=" + post.getSchoolNo();
    }





  @GetMapping("list")
  public void list(Model model, int schoolNo,
                    HttpSession httpSession,
                    @AuthenticationPrincipal PrincipalDetails principalDetails,
                    @LoginUser User loginUser
  ) {

    System.out.println(postService.findBySchoolPostList(schoolNo));
    log.debug(postService.findBySchoolPostList(schoolNo));

    log.debug(schoolUserService.findBySchoolUserList(schoolNo));
    System.out.println(schoolUserService.findBySchoolUserList(schoolNo));
    List<Post> posts = postService.findBySchoolPostList(schoolNo);
    Post post = postService.findByFixList(schoolNo);

    System.out.print("@@@@@@@@@@@@@@@@@@@" + posts);
    model.addAttribute("schoolNo", schoolNo);
    model.addAttribute("postlists", posts);
    model.addAttribute("schoolUsers", schoolUserService.findBySchoolUserList(schoolNo));
    model.addAttribute("fixlist", post);
  }


    @GetMapping("view/{lNo}")
    @ResponseBody
    public Object findByPost(int no, @PathVariable String lNo) throws Exception {

        int schoolNo = Integer.parseInt(lNo);
        log.debug(postService.get(no, schoolNo));
        Post post = postService.get(no, schoolNo);
        List<AttachedFile> attachedFiles = postService.getAttachedFiles(no);
        List<Comment> comments = postService.getComments(no);

        if (post == null) {
            throw new Exception("게시글 번호가 유효하지 않습니다.");
        }
        System.out.println(post);
        System.out.println(attachedFiles);
        System.out.println(comments);

        Map<String,Object> result = new HashMap<>();
        result.put("comments", comments);
        result.put("files", attachedFiles);
        result.put("post", post);
        return result;
    }




    // 검색창에 필터로 검색했을 때
    @PostMapping("search")
    @GetMapping("search")
    public String searchPosts(
            int schoolNo,
            @RequestParam("keyword") String keyword,
            @RequestParam("filter") String filter,
            Model model) {
        if (filter.equals("0")) { // 내용으로 검색
            List<Post> postList = postService.findBySchoolContent(schoolNo, keyword);
            model.addAttribute("postList", postList);
        } else {                  // 작성자로 검색
            List<Post> postList = postService.findBySchoolUserName(schoolNo, keyword);
            model.addAttribute("postList", postList);
        }
        return "post/list";
    }

    @PostMapping("update")
    public String update(
            Post post,
            MultipartFile[] files,
            HttpSession session) throws Exception {

//    User loginUser = (User) session.getAttribute("loginUser");
//    if (loginUser == null) {
//      throw new Exception("로그인하시기 바랍니다!");
//    }
//
//    Post old = postService.get(post.getNo());
//    if (old == null) {
//      throw new Exception("번호가 유효하지 않습니다.");
//
//    } else if (old.getNo() != loginUser.getNo()) {
//      throw new Exception("권한이 없습니다.");
//    }



        ArrayList<AttachedFile> fileList = new ArrayList<>();
        if (fileList != null && fileList.size() > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String filename = this.fileUpload.upload(this.bucketName, this.uploadDir, file);
                    fileList.add(AttachedFile.builder().filePath(filename).build()); // 업로드된 파일 이름 추가
                }
            }
        }

        if (fileList.size() > 0) {
            post.setFileList(fileList);
        }
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@");
        postService.update(post);

        return "redirect:list?schoolNo=" + post.getSchoolNo();

    }


    @GetMapping("delete")
    public String delete(
            Post post,
            @RequestParam("post_no") int no,
            @RequestParam("schoolNo") int schoolNo,
            HttpSession session) throws Exception {


//    User loginUser = (User) session.getAttribute("loginUser");
//    if (loginUser == null) {
//      throw new Exception("로그인하시기 바랍니다!");
//    }
//
//    Post post = postService.get(no);
//    if (post == null) {
//      throw new Exception("번호가 유효하지 않습니다.");
//
//    } else if (post.getNo() != loginUser.getNo()) {
//      throw new Exception("권한이 없습니다.");
//    }
//
    List<AttachedFile> files = postService.getAttachedFiles(no);

    postService.delete(no, schoolNo);

    for (AttachedFile file : files) {
    fileUpload.delete(this.bucketName, this.uploadDir, file.getFilePath());
    }

    return "redirect:list?schoolNo=" + post.getSchoolNo();
  }



    @GetMapping("md/{postNo}")
    public String md(
            @PathVariable("postNo") int no,
            @RequestParam("schoolNo") int schoolNo,
            Model model) throws Exception {
        Post post = postService.findByPost(no, schoolNo);
        model.addAttribute("post", post);
        model.addAttribute("content", post.getContent()); // "content" 데이터 추가


        return "post/md";
    }


    @GetMapping("file/delete")
    public String fileDelete(int no, Post post, HttpSession session) throws Exception {

//    User loginUser = (User) session.getAttribute("loginUser");
//    if (loginUser == null) {
//      throw new Exception("로그인하시기 바랍니다!");
//    }

        AttachedFile fileList = postService.getAttachedFile(no);
        if (fileList == null) {
            throw new Exception("첨부파일 번호가 유효하지 않습니다.");
        }

//    User writer = postService.get(fileList.getPostNo()).getWriter();
//    if (writer.getNo() != loginUser.getNo()) {
//      throw new Exception("권한이 없습니다.");
//    }

        postService.deleteAttachedFile(no);

        //fileUploadHelper.delete(this.bucketName, this.uploadDir, file.getFilePath());

        return "redirect:list?schoolNo=" + post.getSchoolNo();
    }

    @PostMapping("file/upload")
    @ResponseBody
    public Object fileUpload(
            MultipartFile[] files,
            HttpSession session,
            Model model) throws Exception {

        //    User loginUser = (User) session.getAttribute("loginUser");
//    if (loginUser == null) {
//      throw new Exception("로그인하시기 바랍니다!");
//    }

        // FileUpoladHelper Object Storage에 저장한 파일의 이미지 이름을 보관할 컬렉션을 준비한다.
        ArrayList<AttachedFile> fileList = new ArrayList<>();

        // 클라이언트가 보낸 멀티파트 파일을 FileUpoladHelper Object Storage에 업로드한다.
        for (MultipartFile file : files) {
            if (file.getSize() == 0) {
                continue;
            }
            String filename = fileUpload.upload(this.bucketName, this.uploadDir, file);
            fileList.add(AttachedFile.builder().filePath(filename).build());
        }

        // 업로드한 파일 목록을 세션에 보관한다.
        ArrayList<AttachedFile> oldfileList = (ArrayList<AttachedFile>) session.getAttribute("attachedFiles");
        if (oldfileList != null) {
            oldfileList.addAll(fileList);
            model.addAttribute("fileList", oldfileList);
        } else {
            model.addAttribute("fileList", fileList);
        }

        // 클라이언트에서 이미지 이름을 가지고 <img> 태그를 생성할 수 있도록
        // 업로드한 파일의 이미지 정보를 보낸다.
        return fileList;
    }
}