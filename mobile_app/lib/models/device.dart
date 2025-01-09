class Device {
  final String associatedIP;
  final String associatedMAC;
  final int heightResolution;
  final int widthResolution;
  final bool recordingMode;
  final bool recordingVideo;

  Device({
    required this.associatedIP,
    required this.associatedMAC,
    required this.heightResolution,
    required this.widthResolution,
    required this.recordingMode,
    required this.recordingVideo,
  });

  Map<String, dynamic> toJson() {
    return {
      'AssociatedIP': associatedIP,
      'AssociatedMAC': associatedMAC,
      'heightResolution': heightResolution,
      'widthResolution': widthResolution,
      'recordingMode': recordingMode,
      'recordingVideo': recordingVideo,
    };
  }
}
